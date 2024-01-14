package de.Stupidus.Framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandFW {
    String name();
    String errorPermission() default "No permission for: {permission}";
    String errorPlayer() default "You must be a player to perfom this command!";
    String existPlayer() default "This player doesn't exist!";
    String permission() default "";
    boolean player() default false;

    String unknowCommand() default "Unknown command!";
}
