package com.slayfx.logic.map;

import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.HexColor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class MapReader {
    private MapReader(){} // prevent initialization by client code

    public static ArrayList<Hex> read(String file){
        ArrayList<Hex> hexMap = new ArrayList<>();

        String line = null;
        try{
            // To read text files
            FileReader fileReader = new FileReader(file);

            // To read whole lines instead of reading character by character
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null){
                System.out.println(line);

                // Parse line
                String[] parts = line.split(";", 2);

                int x_ = Integer.valueOf(parts[0]);
                int y_ = Integer.valueOf(parts[1]);
                hexMap.add(new Hex(HexColor.GREEN, x_, y_));
            }
            bufferedReader.close();
        }
        catch (FileNotFoundException ex){
            System.out.println("Unable to open file " + file);
        }
        catch (IOException ex){
            System.out.println("Error reading file " + file);
        }

        return hexMap;
    }
}
