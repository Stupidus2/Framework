import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
@LoadOrder(PluginLoadOrder.STARTUP)
@Plugin(name = "TestPlugin", version = "1.0")
@org.bukkit.plugin.java.annotation.command.Command(name = "panda")
@org.bukkit.plugin.java.annotation.command.Command(name = "hi")
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        CommandFrameworkInitializer commandFrameworkInitializer = new CommandFrameworkInitializer(this);
    }
}
