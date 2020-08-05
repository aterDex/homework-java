package ru.otus.homework.herald.core;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

public class HeraldUtils {

    private HeraldUtils() {
    }

    public static void setupILoadIntoStack(int access, Type[] types, MethodVisitor visitor) {
        int idxOnStack = Modifier.isStatic(access) ? 0 : 1;
        for (int i = 0; i < types.length; i++) {
            visitor.visitVarInsn(types[i].getOpcode(Opcodes.ILOAD), idxOnStack);
            idxOnStack += types[i].getSize();
        }
    }

    /**
     * Резолвим имя переменной
     *
     * @param idx        порядковый номер переменной начиная с 1
     * @param idxOnStack номер перменной в стэке
     * @param heraldMeta метаинформация по колонкам
     * @return имя для переменной
     */
    public static String resolveName(int idx, int idxOnStack, HeraldMeta heraldMeta) {
        if (heraldMeta != null) {
            return heraldMeta.getLocalVariables().stream()
                    .filter(x -> x.getIndex() == idxOnStack)
                    .findAny()
                    .map(LocalVariableMeta::getName)
                    .orElse("par" + idx);
        }
        return "par" + idx;
    }
}
