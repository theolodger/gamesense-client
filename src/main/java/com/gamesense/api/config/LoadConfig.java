package com.gamesense.api.config;

import java.awt.Font;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.FixedComponent;

/**
 * @Author Hoosiers on 10/15/2020
 */

public class LoadConfig {

    public LoadConfig(){
        try {
            loadConfig();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    String fileName = "GameSense/";
    String moduleName = "Modules/";
    String mainName = "Main/";
    String miscName = "Misc/";

    public void loadConfig() throws IOException {
    	loadModules();
        loadEnabledModules();
        loadModuleKeybinds();
        loadDrawnModules();
        loadCommandPrefix();
        loadCustomFont();
        loadFriendsList();
        loadEnemiesList();
        loadClickGUIPositions();
        loadAutoGG();
        loadAutoReply();
    }

    //big shoutout to lukflug for helping/fixing this
    public void loadModules() {
        String moduleLocation = fileName + moduleName;

        for (Module module : ModuleManager.getModules()){
            try {
                loadModuleDirect(moduleLocation, module);
            }
            catch (IOException e){
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }

    public void loadModuleDirect(String moduleLocation, Module module) throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Module") == null){
            return;
        }

        JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        for (Setting setting : GameSenseMod.getInstance().settingsManager.getSettingsForMod(module)){
            JsonElement dataObject = settingObject.get(setting.getConfigName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                switch (setting.getType()){
                    case BOOLEAN:
                        ((Setting.Boolean) setting).setValue(dataObject.getAsBoolean());
                        break;
                    case INTEGER:
                        ((Setting.Integer) setting).setValue(dataObject.getAsInt());
                        break;
                    case DOUBLE:
                        ((Setting.Double) setting).setValue(dataObject.getAsDouble());
                        break;
                    case COLOR:
                        ((Setting.ColorSetting) setting).fromInteger(dataObject.getAsInt());
                        break;
                    case MODE:
                        ((Setting.Mode) setting).setValue(dataObject.getAsString());
                        break;
                }
            }
        }
        inputStream.close();
    }

    public void loadEnabledModules() throws IOException {
        String enabledLocation = fileName + mainName;

        if (!Files.exists(Paths.get(enabledLocation + "Toggle" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()){
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                if (dataObject.getAsBoolean() == true){
                    module.enable();
                }
            }
        }
        inputStream.close();
    }

    public void loadModuleKeybinds() throws IOException {
        String bindLocation = fileName + mainName;

        if (!Files.exists(Paths.get(bindLocation + "Bind" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(bindLocation + "Bind" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()){
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setBind(dataObject.getAsInt());
            }
        }
        inputStream.close();
    }

    public void loadDrawnModules() throws IOException {
        String drawnLocation = fileName + mainName;

        if (!Files.exists(Paths.get(drawnLocation + "Drawn" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(drawnLocation + "Drawn" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()){
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setDrawn(dataObject.getAsBoolean());
            }
        }
        inputStream.close();
    }

    public void loadCommandPrefix() throws IOException {
        String prefixLocation = fileName + mainName;

        if (!Files.exists(Paths.get(prefixLocation + "CommandPrefix" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(prefixLocation + "CommandPrefix" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Prefix") == null) {
            return;
        }

        JsonElement prefixObject = mainObject.get("Prefix");

        if (prefixObject != null && prefixObject.isJsonPrimitive()) {
            Command.setCommandPrefix(prefixObject.getAsString());
        }
        inputStream.close();
    }

    public void loadCustomFont() throws IOException {
        String fontLocation = fileName + miscName;

        if (!Files.exists(Paths.get(fontLocation + "CustomFont" + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fontLocation + "CustomFont" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Font Name") == null || mainObject.get("Font Size") == null){
            return;
        }

        JsonElement fontNameObject = mainObject.get("Font Name");

        String name = null;

        if (fontNameObject != null && fontNameObject.isJsonPrimitive()) {
            name = fontNameObject.getAsString();
        }

        JsonElement fontSizeObject = mainObject.get("Font Size");

        int size = -1;

        if (fontSizeObject != null && fontSizeObject.isJsonPrimitive()) {
            size = fontSizeObject.getAsInt();
        }

        if (name != null && size != -1){
            GameSenseMod.fontRenderer = new CFontRenderer(new Font(name, Font.PLAIN, size), true, true);
            GameSenseMod.fontRenderer.setFont(new Font(name, Font.PLAIN, size));
            GameSenseMod.fontRenderer.setAntiAlias(true);
            GameSenseMod.fontRenderer.setFractionalMetrics(true);
            GameSenseMod.fontRenderer.setFontName(name);
            GameSenseMod.fontRenderer.setFontSize(size);
        }
        inputStream.close();
    }

    public void loadFriendsList() throws IOException {
        String friendLocation = fileName + miscName;

        if (!Files.exists(Paths.get(friendLocation + "Friends" + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "Friends" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Friends") == null){
            return;
        }

        JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();

        friendObject.forEach(object -> {
            Friends.addFriend(object.getAsString());
        });
        inputStream.close();
    }

    public void loadEnemiesList() throws IOException {
        String enemyLocation = fileName + miscName;

        if (!Files.exists(Paths.get(enemyLocation + "Enemies" + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(enemyLocation + "Enemies" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Enemies") == null){
            return;
        }

        JsonArray enemyObject = mainObject.get("Enemies").getAsJsonArray();

        enemyObject.forEach(object -> {
            Enemies.addEnemy(object.getAsString());
        });
        inputStream.close();
    }

    public void loadClickGUIPositions() throws IOException {
        String fileLocation = fileName + mainName;

        if (!Files.exists(Paths.get(fileLocation + "ClickGUI" + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "ClickGUI" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Panels") == null){
            return;
        }

        JsonObject panelObject = mainObject.get("Panels").getAsJsonObject();
        for (FixedComponent frames : GameSenseMod.getInstance().clickGUI.gui.getComponents()){
            if (panelObject.get(frames.getTitle()) == null){
                return;
            }

            JsonObject panelObject2 = panelObject.get(frames.getTitle()).getAsJsonObject();

            Point point=new Point();
            JsonElement panelPosXObject = panelObject2.get("PosX");
            if (panelPosXObject != null && panelPosXObject.isJsonPrimitive()){
                point.x=panelPosXObject.getAsInt();
            }

            JsonElement panelPosYObject = panelObject2.get("PosY");
            if (panelPosYObject != null && panelPosYObject.isJsonPrimitive()){
                point.y=panelPosYObject.getAsInt();
            }
            frames.setPosition(GameSenseMod.getInstance().clickGUI,point);

            JsonElement panelOpenObject = panelObject2.get("State");
            if (panelOpenObject != null && panelOpenObject.isJsonPrimitive()){
                if (frames instanceof DraggableContainer && panelOpenObject.getAsBoolean()) ((DraggableContainer)frames).open.toggle();
            }
        }
        inputStream.close();
    }

    public void loadAutoGG() throws IOException {
        String fileLocation = fileName + miscName;

        if (!Files.exists(Paths.get(fileLocation + "AutoGG" + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoGG" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Messages") == null){
            return;
        }

        JsonArray messageObject = mainObject.get("Messages").getAsJsonArray();

        messageObject.forEach(object -> {
            AutoGG.addAutoGgMessage(object.getAsString());
        });
        inputStream.close();
    }

    public void loadAutoReply() throws IOException {
        String fileLocation = fileName + miscName;

        if (!Files.exists(Paths.get(fileLocation + "AutoReply" + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoReply" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("AutoReply") == null){
            return;
        }

        JsonObject arObject = mainObject.get("AutoReply").getAsJsonObject();
        JsonElement dataObject = arObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoReply.setReply(dataObject.getAsString());
        }
        inputStream.close();
    }
}