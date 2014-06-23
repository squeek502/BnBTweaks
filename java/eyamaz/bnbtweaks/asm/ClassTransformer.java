package eyamaz.bnbtweaks.asm;

import static org.objectweb.asm.Opcodes.*;
import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
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

			methodNode = findMethodNodeOfClass(classNode, "_addMeltingOreRecipe", "(Ljava/lang/String;Ljava/lang/String;III)V");
			if (methodNode != null)
			{
				fixExtraTiCMelting(methodNode);
			}

			return writeClassToBytes(classNode);
		}
		
		if (name.equals("hostileworlds.dimension.gen.MapGenSchematics"))
		{
			ModBnBTweaks.Log.info("Patching HostileWorld's MapGenSchematis....");
			
			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, "genTemple", "(Lnet/minecraft/world/World;II[B)V");
			if (methodNode != null)
			{
				fixHostileWorldsMapGenSchematics(methodNode);
			}
			
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
				ModBnBTweaks.Log.info(" Found target method: " + methodName);
				return method;
			}
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

		ModBnBTweaks.Log.info(" Patched " + method.name);
	}
	
	public void fixHostileWorldsMapGenSchematics(MethodNode method)
	{
		Iterator<AbstractInsnNode> iter = method.instructions.iterator();
		
		AbstractInsnNode currentNode = null;
		int index = -1;
		
		//Iterate through all instructions within method
		//Delete all instructions without Opcode -1 or 177
		
		while (iter.hasNext())
		{
			index++;
			currentNode = iter.next();
			
			if (currentNode.getOpcode() != -1 && currentNode.getOpcode() != 177)
			{
				method.instructions.remove(currentNode);
			}
		}
		
		ModBnBTweaks.Log.info(" Patched " + method.name);
	}
}
