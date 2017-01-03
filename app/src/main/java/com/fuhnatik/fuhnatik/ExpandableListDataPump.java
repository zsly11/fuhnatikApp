package com.fuhnatik.fuhnatik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by mattginsberg on 7/25/16.
 */
public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> playerPositions = new ArrayList<String>();
        playerPositions.add("All Positions");
        playerPositions.add("Quaterbacks");
        playerPositions.add("Running Backs");
        playerPositions.add("Wide Receivers");
        playerPositions.add("Tight Ends");
        playerPositions.add("Kickers");
        playerPositions.add("Defences");

        expandableListDetail.put("Select a position", playerPositions);
        return expandableListDetail;
    }
}