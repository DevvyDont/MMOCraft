package me.devvy.mmocraft.storage;

import me.devvy.mmocraft.MMOCraft;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SpreadsheetParser {

    public static void copyDefaultFile(String filename) throws IOException {
        Files.copy(MMOCraft.getInstance().getResource(filename), Path.of(MMOCraft.getInstance().getDataFolder().getAbsolutePath(), filename), StandardCopyOption.REPLACE_EXISTING);
    }

    public static InputStream getFileInputStream(String path) {
        try {
            File file = new File(path);
            return new FileInputStream(file);
        } catch (FileNotFoundException ignored) {}

        return null;
    }

    public static List<String> getLinesFromFile(String filename) {

        List<String> lines = new ArrayList<>();

        File dataFolder = new File(MMOCraft.getInstance().getDataFolder().getAbsolutePath());
        if (!dataFolder.exists())
            dataFolder.mkdir();

        File file = new File(MMOCraft.getInstance().getDataFolder().getAbsolutePath(), filename);

        // If the file doesn't exist, then that means that we need to make a default
        if (!file.exists()) {
            try {
                copyDefaultFile(filename);
            } catch (IOException e) {
                MMOCraft.getInstance().getLogger().severe("Failed to load default file " + filename + "!");
                e.printStackTrace();
                return lines;
            }
        }

        InputStream is = getFileInputStream(MMOCraft.getInstance().getDataFolder().getAbsolutePath() + "/" + filename);

        if (is == null) {
            MMOCraft.getInstance().getLogger().severe("Could not find creatures.csv, does it exist?");
            return lines;
        }

        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            String line;
            while ((line = br.readLine()) != null)
                lines.add(line);

            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static List<List<String>> tokenizeCommaSeparatedLines(List<String> lines) {

        List<List<String>> listOfRowsOfTokens = new ArrayList<>();

        for (String line : lines) {
            // Transform the comma separated values into separate strings
            List<String> thisLinesTokens = List.of(line.split(","));
            listOfRowsOfTokens.add(thisLinesTokens);
        }
        return listOfRowsOfTokens;
    }

    public static void initializeSpreadsheets() {

        // Load up the csv file for creatures
        List<List<String>> tokenizedCreatureCsv = tokenizeCommaSeparatedLines(getLinesFromFile("creatures.csv"));
        // Remove the header
        if (!tokenizedCreatureCsv.isEmpty())
            tokenizedCreatureCsv.remove(0);
        // For every row, add the entity parsed
        for (List<String> row : tokenizedCreatureCsv)
            EntityAttributesStorage.storeFromCSVRow(row);

        MMOCraft.getInstance().getLogger().info("Successfully loaded attributes of " + EntityAttributesStorage.getAllRegisteredEntityAttributes().size() + " custom entities!");

        // Load up the csv file for items
        List<List<String>> tokenizedItemCsv = tokenizeCommaSeparatedLines(getLinesFromFile("items.csv"));
        // Remove the header
        if (!tokenizedItemCsv.isEmpty())
            tokenizedItemCsv.remove(0);

        // For every row, add the parsed item
        for (List<String> row : tokenizedItemCsv)
            ItemAttributesStorage.storeFromCSVRow(row);

        MMOCraft.getInstance().getLogger().info("Successfully loaded attributes of " + ItemAttributesStorage.getAllRegisteredItemAttributes().size() + " custom items!");
    }

    public static void deleteSpreadsheets () {

        new File(MMOCraft.getInstance().getDataFolder().getAbsolutePath() + "/creatures.csv").delete();
        new File(MMOCraft.getInstance().getDataFolder().getAbsolutePath() + "/items.csv").delete();

    }

}
