package com.rcaloras.roo.addon.timestamp;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Sample of a command class. The command class is registered by the Roo shell following an
 * automatic classpath scan. You can provide simple user presentation-related logic in this
 * class. You can return any objects from each method, or use the logger directly if you'd
 * like to emit messages of different severity (and therefore different colours on 
 * non-Windows systems).
 * 
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class TimestampCommands implements CommandMarker { // All command types must implement the CommandMarker interface
    
    /**
     * Get a reference to the TimestampOperations from the underlying OSGi container
     */
    @Reference private TimestampOperations operations;
    
    /**
     * This method is optional. It allows automatic command hiding in situations when the command should not be visible.
     * For example the 'entity' command will not be made available before the user has defined his persistence settings 
     * in the Roo shell or directly in the project.
     * 
     * You can define multiple methods annotated with {@link CliAvailabilityIndicator} if your commands have differing
     * visibility requirements.
     * 
     * @return true (default) if the command should be visible at this stage, false otherwise
     */
    @CliAvailabilityIndicator({ "timestamp setup", "timestamp add", "timestamp all" })
    public boolean isCommandAvailable() {
        return operations.isCommandAvailable();
    }
    
    @CliCommand(value = "timestamp add", help = "Adds @RooTimestamp to target type")
    public void add(@CliOption(key = "type", mandatory = true, help = "The java type to apply this annotation to") JavaType target) {
        operations.annotateType(target);
    }
    
    @CliCommand(value = "timestamp all", help = "Adds @RooTimestamp to all types annotated with @RooJavaBean" )
    public void all() {
        operations.annotateAll();
    }
    
    @CliCommand(value = "timestamp setup", help = "Setup Timestamp addon")
    public void setup() {
        operations.setup();
    }
    
    @CliCommand(value = "timestamp remove", help = "Remove all dependencies and removes all annotations of @RooTimestamp")
    public void remove() {
        operations.remove();
  
    }
    
}