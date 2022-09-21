package me.hasenzahn1.structurereloot.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassDebug {

    private final Object object;

    public ClassDebug(Object obejct){
        this.object = obejct;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( object.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = object.getClass().getDeclaredFields();

        //print field names paired with their values
        result.append("  Fields: ").append(newLine);
        for ( Field field : fields  ) {
            result.append("    ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(object) );
            } catch ( IllegalAccessException ex ) {
                //System.out.println(ex);
                field.setAccessible(true);
                try {
                    result.append( field.get(object) );
                } catch (IllegalAccessException e) {
                    System.out.println("Something wrong happened");
                }
            }
            result.append(newLine);
        }

        result.append(newLine).append("  Methods: ").append(newLine);

        for(Method method : object.getClass().getDeclaredMethods()){
            result.append("    ").append(method.getName()).append(": ").append(method).append(newLine);
        }

        result.append("}");


        return result.toString();
    }
}
