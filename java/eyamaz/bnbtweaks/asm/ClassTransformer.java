package eyamaz.bnbtweaks.asm;

import static org.objectweb.asm.Opcodes.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import eyamaz.bnbtweaks.ModBnBTweaks;

public class ClassTransformer implements IClassTransformer
{

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.equals("glassmaker.extratic.common.RecipeHandler"))
		{
			ModBnBTweaks.Log.info("Patching ExtraTiC's RecipeHandler...");

			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, "_addMeltingOreRecipe", "(Ljava/lang/String;Ljava/lang/String;II)V");
			if (methodNode != null)
			{
				fixExtraTiCMelting(methodNode);
			}
			else
				throw new RuntimeException("Could not find _addMeltingOreRecipe(II) method in ExtraTiC RecipeHandler");

			methodNode = findMethodNodeOfClass(classNode, "_addMeltingOreRecipe", "(Ljava/lang/String;Ljava/lang/String;III)V");
			if (methodNode != null)
			{
				fixExtraTiCMelting(methodNode);
			}
			else
				throw new RuntimeException("Could not find _addMeltingOreRecipe(III) method in ExtraTiC RecipeHandler");

			return writeClassToBytes(classNode);
		}

		if (name.equals("hostileworlds.dimension.gen.MapGenSchematics"))
		{
			ModBnBTweaks.Log.info("Patching HostileWorld's MapGenSchematics....");

			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, "genTemple", "(Lnet/minecraft/world/World;II[B)V");
			if (methodNode != null)
			{
				stopPyramidGeneration(methodNode);
			}
			else
				throw new RuntimeException("Could not find genTemple method in HostileWorlds MapGenSchematics");

			return writeClassToBytes(classNode);
		}

		if (transformedName.equals("net.minecraft.block.material.MaterialPortal"))
		{
			boolean isObfuscated = !name.equals(transformedName);

			ModBnBTweaks.Log.info("Patching Minecraft MaterialPortal");

			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, isObfuscated ? "a" : "isSolid", "()Z");
			if (methodNode != null)
			{
				makePortalsSolidToFluid(methodNode);
			}
			else
				throw new RuntimeException("Could not find isSolid method in MaterialPortal");

			return writeClassToBytes(classNode);
		}

		if (transformedName.equals("net.minecraft.tileentity.MobSpawnerBaseLogic"))
		{
			boolean isObfuscated = !name.equals(transformedName);

			ModBnBTweaks.Log.info("Patching Minecraft MobSpawnerBaseLogic");

			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, isObfuscated ? "g" : "updateSpawner", "()V");
			if (methodNode != null)
			{
				captureIsSpawningFromSpawner(methodNode);
			}
			else
				throw new RuntimeException("Could not find updateSpawner method in MobSpawnerBaseLogic");

			return writeClassToBytes(classNode);
		}

		if (transformedName.equals("net.minecraft.entity.monster.EntityMob"))
		{
			boolean isObfuscated = !name.equals(transformedName);

			ModBnBTweaks.Log.info("Patching Minecraft EntityMob");

			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, isObfuscated ? "bs" : "getCanSpawnHere", "()Z");
			if (methodNode != null)
			{
				makeEntityMobIgnoreLightLevel(methodNode);
			}
			else
				throw new RuntimeException("Could not find getCanSpawnHere method in EntityMob");

			methodNode = findMethodNodeOfClass(classNode, isObfuscated ? "a" : "getBlockPathWeight", "(III)F");
			if (methodNode != null)
			{
				makeEntityMobIgnoreWorldLightLevel(methodNode);
			}
			else
				throw new RuntimeException("Could not find getBlockPathWeight method in EntityMob");

			return writeClassToBytes(classNode);
		}
		
		if (name.equals("lycanite.lycanitesmobs.api.entity.EntityCreatureBase"))
		{
			ModBnBTweaks.Log.info("Patching LycanitesMobs EntityCreatureBase");
			
			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, "fixedSpawnCheck", "(Lnet/minecraft/world/World;III)Z");
			if (methodNode != null)
			{
				makeLycanitesMobsIgnoreLightLevel(methodNode);
			}
			else 
				throw new RuntimeException("Could not find fixedSpawnCheck in EntityCreatureBase");
			
			return writeClassToBytes(classNode);
		}

		return bytes;
	}

	private ClassNode readClassFromBytes(byte[] bytes)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClassToBytes(ClassNode classNode)
	{
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc)
	{
		for (MethodNode method : classNode.methods)
		{
			if (method.name.equals(methodName) && method.desc.equals(methodDesc))
			{
				return method;
			}
		}
		return null;
	}

	private AbstractInsnNode findFirstInstruction(MethodNode method)
	{
		for (AbstractInsnNode instruction : method.instructions.toArray())
		{
			if (instruction.getType() != AbstractInsnNode.LABEL && instruction.getType() != AbstractInsnNode.LINE)
				return instruction;
		}
		return null;
	}

	private AbstractInsnNode findFirstInstructionOfType(MethodNode method, int bytecode)
	{
		for (AbstractInsnNode instruction : method.instructions.toArray())
		{
			if (instruction.getOpcode() == bytecode)
				return instruction;
		}
		return null;
	}

	private AbstractInsnNode findLastInstructionOfType(MethodNode method, int bytecode)
	{
		for (AbstractInsnNode instruction = method.instructions.getLast(); instruction != null; instruction = instruction.getPrevious())
		{
			if (instruction.getOpcode() == bytecode)
				return instruction;
		}
		return null;
	}

	private AbstractInsnNode findChronoInstructionOfType(MethodNode method, int bytecode, int number)
	{
		int i = 0;
		for (AbstractInsnNode instruction : method.instructions.toArray())
		{
			if (instruction.getOpcode() == bytecode && ++i == number)
				return instruction;
		}
		return null;
	}

	public void fixExtraTiCMelting(MethodNode method)
	{
		AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);

		InsnList toInject = new InsnList();

		/*
		String par1 = "";
		int par3 = 0;
		// equivalent to:
		if (par1.startsWith("ore"))
			par3 = tconstruct.util.config.PHConstruct.ingotsPerOre;
		*/

		toInject.add(new VarInsnNode(ALOAD, 0));
		toInject.add(new LdcInsnNode("ore"));
		toInject.add(new MethodInsnNode(INVOKEVIRTUAL, String.class.getName().replace('.', '/'), "startsWith", "(Ljava/lang/String;)Z"));
		LabelNode labelIfNotStartsWith = new LabelNode();
		toInject.add(new JumpInsnNode(IFEQ, labelIfNotStartsWith));
		toInject.add(new FieldInsnNode(GETSTATIC, "tconstruct/util/config/PHConstruct", "ingotsPerOre", "I"));
		toInject.add(new VarInsnNode(ISTORE, 2));
		toInject.add(labelIfNotStartsWith);

		method.instructions.insertBefore(targetNode, toInject);

		ModBnBTweaks.Log.info("Patched " + method.name);
	}

	public void stopPyramidGeneration(MethodNode method)
	{
		AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);

		InsnList toInject = new InsnList();

		//Add return statement to beginning of method

		toInject.add(new InsnNode(RETURN));

		method.instructions.insertBefore(targetNode, toInject);

		ModBnBTweaks.Log.info("Patched " + method.name);
	}

	public void makePortalsSolidToFluid(MethodNode method)
	{
		AbstractInsnNode targetNode = findFirstInstructionOfType(method, ICONST_0);

		InsnList toInject = new InsnList();

		//Change portals isSolid to return true, rather than false
		//Causing liquids to no longer break them

		toInject.add(new InsnNode(ICONST_1));

		method.instructions.insert(targetNode, toInject);
		method.instructions.remove(targetNode);

		ModBnBTweaks.Log.info("Patched: " + method.name);
	}

	public void captureIsSpawningFromSpawner(MethodNode method)
	{
		AbstractInsnNode firstNode = findFirstInstruction(method);
		AbstractInsnNode lastNode = findLastInstructionOfType(method, RETURN);

		if (firstNode == null || lastNode == null || lastNode.getOpcode() != RETURN)
			throw new RuntimeException("Could not find target nodes for MobSpawnerBaseLogic patch");

		InsnList firstInject = new InsnList();
		InsnList lastInject = new InsnList();

		//Inject Hooks.isSpawningFromSpawner = true; to start
		//inject Hooks.isSpawningFromSpawner = false; to end

		firstInject.add(new InsnNode(ICONST_1));
		firstInject.add(new FieldInsnNode(PUTSTATIC, "eyamaz/bnbtweaks/asm/Hooks", "isSpawningFromSpawner", "Z"));

		lastInject.add(new InsnNode(ICONST_0));
		lastInject.add(new FieldInsnNode(PUTSTATIC, "eyamaz/bnbtweaks/asm/Hooks", "isSpawningFromSpawner", "Z"));

		method.instructions.insertBefore(firstNode, firstInject);
		method.instructions.insertBefore(lastNode, lastInject);

		ModBnBTweaks.Log.info("Patched: " + method.name);
	}

	public void makeEntityMobIgnoreLightLevel(MethodNode method)
	{
		AbstractInsnNode firstTargetNode = findChronoInstructionOfType(method, ALOAD, 2);
		AbstractInsnNode secondTargetNode = findChronoInstructionOfType(method, ALOAD, 3);

		if (firstTargetNode == null || secondTargetNode == null)
			throw new RuntimeException("Could not find target nodes for EntityMob." + method.name + " patch");

		InsnList firstInject = new InsnList();
		InsnList secondInject = new InsnList();

		//Inject hook to create
		//return this.worldObj.difficultySetting > 0 && (Hooks.isSpawningFromSpawner || this.isValidLightLevel()) && super.getCanSpawnHere();

		firstInject.add(new FieldInsnNode(GETSTATIC, "eyamaz/bnbtweaks/asm/Hooks", "isSpawningFromSpawner", "Z"));
		LabelNode label = new LabelNode();
		firstInject.add(new JumpInsnNode(IFNE, label));

		secondInject.add(label);
		secondInject.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

		method.instructions.insertBefore(firstTargetNode, firstInject);
		method.instructions.insertBefore(secondTargetNode, secondInject);

		ModBnBTweaks.Log.info("Patched: " + method.name);
	}

	public void makeEntityMobIgnoreWorldLightLevel(MethodNode method)
	{
		AbstractInsnNode targetNode = findFirstInstruction(method);

		if (targetNode == null)
			throw new RuntimeException("Could not find target node for EntityMob." + method.name + " patch");

		InsnList toInject = new InsnList();

		/*
		// equivalent to:
		if (Hooks.isSpawningFromSpawner)
			return 0f;
		*/

		toInject.add(new FieldInsnNode(GETSTATIC, "eyamaz/bnbtweaks/asm/Hooks", "isSpawningFromSpawner", "Z"));
		LabelNode label = new LabelNode();
		toInject.add(new JumpInsnNode(IFEQ, label));
		toInject.add(new InsnNode(FCONST_0));
		toInject.add(new InsnNode(FRETURN));
		toInject.add(label);

		method.instructions.insertBefore(targetNode, toInject);

		ModBnBTweaks.Log.info("Patched: " + method.name);
	}
	
	public void makeLycanitesMobsIgnoreLightLevel(MethodNode method)
	{
		AbstractInsnNode firstTargetNode = findFirstInstructionOfType(method, IF_ICMPLE);
		AbstractInsnNode secondTargetNode = findFirstInstructionOfType(method, IF_ICMPGE);
		AbstractInsnNode thirdTargetNode = findFirstInstructionOfType(method, IRETURN);
		AbstractInsnNode fourthTargetNode = findChronoInstructionOfType(method, IRETURN, 2);
		
		if (firstTargetNode == null || secondTargetNode == null || thirdTargetNode == null || fourthTargetNode == null)
			throw new RuntimeException("Could not find target node for EntityCreatureBase" + method.name + "patch");
		
		InsnList firstInject = new InsnList();
		InsnList secondInject = new InsnList();
		InsnList firstLabelInject = new InsnList();
		InsnList secondLabelInject = new InsnList();
		
		/*
		 * Equivalent to:                                           |          Injection             |
		if(this.spawnsInDarkness && this.testLightLevel(i, j, k) > 1 && !Hooks.isSpawningFromSpawner)
    		return false;
    	if(this.spawnsOnlyInLight && this.testLightLevel(i, j, k) < 2 && !Hooks.isSpawningFromSpawner)
    		return false;
		*/
		
		firstInject.add(new FieldInsnNode(GETSTATIC, "eyamaz/bnbtweaks/asm/Hooks", "isSpawningFromSpawner", "Z"));
		LabelNode label1 = new LabelNode();
		firstInject.add(new JumpInsnNode(IFNE, label1));
		
		secondInject.add(new FieldInsnNode(GETSTATIC, "eyamaz/bnbtweaks/asm/Hooks", "isSpawningFromSpawner", "Z"));
		LabelNode label2 = new LabelNode();
		secondInject.add(new JumpInsnNode(IFNE, label2));
		
		firstLabelInject.add(label1);
		secondLabelInject.add(label2);
		
		method.instructions.insert(firstTargetNode, firstInject);
		method.instructions.insert(secondTargetNode, secondInject);
		method.instructions.insert(thirdTargetNode, firstLabelInject);
		method.instructions.insert(fourthTargetNode, secondLabelInject);
		
		ModBnBTweaks.Log.info("Patched: " + method.name);
	}
}
