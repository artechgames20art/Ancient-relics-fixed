package com.ancientrelics.plugin.gui;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.gui.pages.AdminPage;
import com.ancientrelics.plugin.gui.pages.AllRelicsPage;
import com.ancientrelics.plugin.gui.pages.HomePage;
import com.ancientrelics.plugin.gui.pages.InformationPage;
import com.ancientrelics.plugin.gui.pages.MyRelicPage;
import com.ancientrelics.plugin.gui.pages.OwnersPage;
import com.ancientrelics.plugin.gui.pages.SettingsPage;
import org.bukkit.entity.Player;

/**
 * Entry point for opening any GUI page. Each concrete page class
 * builds its own Inventory; this manager just dispatches based on
 * {@link GuiPage}.
 */
public class GuiManager {

    private final AncientRelicsPlugin plugin;

    private final HomePage homePage;
    private final MyRelicPage myRelicPage;
    private final AllRelicsPage allRelicsPage;
    private final OwnersPage ownersPage;
    private final InformationPage informationPage;
    private final SettingsPage settingsPage;
    private final AdminPage adminPage;

    public GuiManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
        this.homePage = new HomePage(plugin);
        this.myRelicPage = new MyRelicPage(plugin);
        this.allRelicsPage = new AllRelicsPage(plugin);
        this.ownersPage = new OwnersPage(plugin);
        this.informationPage = new InformationPage(plugin);
        this.settingsPage = new SettingsPage(plugin);
        this.adminPage = new AdminPage(plugin);
    }

    public void openHome(Player player) {
        player.openInventory(homePage.build(player));
    }

    public void openMyRelic(Player player) {
        player.openInventory(myRelicPage.build(player));
    }

    public void openAllRelics(Player player) {
        player.openInventory(allRelicsPage.build(player));
    }

    public void openOwners(Player player) {
        player.openInventory(ownersPage.build(player));
    }

    public void openInformation(Player player, String relicId) {
        player.openInventory(informationPage.build(player, relicId));
    }

    public void openSettings(Player player) {
        player.openInventory(settingsPage.build(player));
    }

    public void openAdmin(Player player) {
        player.openInventory(adminPage.build(player));
    }

    public AncientRelicsPlugin getPlugin() {
        return plugin;
    }
}
