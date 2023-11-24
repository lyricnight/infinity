package me.lyric.infinity.rcefix;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMTransformer implements IClassTransformer {

    public static boolean isObfuscated = false;

    private boolean matches(MethodNode methodNode) {
        if (isObfuscated) {
            return methodNode.name.equals("a") && methodNode.desc.equals("(Lhh;I)V");
        } else {
            return methodNode.name.equals("printChatMessageWithOptionalDeletion") && methodNode.desc.equals("(Lnet/minecraft/util/text/ITextComponent;I)V");
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.gui.GuiNewChat")) {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(basicClass);
            reader.accept(node, ClassReader.EXPAND_FRAMES);

            for (MethodNode methodNode : node.methods) {
                if (matches(methodNode)) {

                    for (int i = 0; i < 13; i++) {
                        methodNode.instructions.remove(methodNode.instructions.get(11));
                    }
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            return writer.toByteArray();
        } else {
            return basicClass;
        }
    }
}