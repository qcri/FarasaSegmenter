/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package farasa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import static farasa.ArabicUtils.buck2morph;
import static farasa.ArabicUtils.utf82buck;

/**
 *
 * @author kareemdarwish
 */
public class FitTemplateClass implements java.io.Serializable {
    private static HashMap<String, Double> hmRoot = new HashMap<String, Double>();
    private static HashMap<String, Double> hmTemplate = new HashMap<String, Double>();
    private static HashMap<Integer, ArrayList<String>> Templates = new HashMap<Integer, ArrayList<String>>();
    private static ArrayList<String> affixes = new ArrayList<String>();

    public FitTemplateClass(String BinDir) throws IOException {
        initVariables(BinDir);
    }
    
    public static void initVariables(String BinDir) throws IOException
    {
        BufferedReader brRoot = new BufferedReader(new InputStreamReader(new FileInputStream(new File(BinDir + "roots.txt")), "UTF8"));
        BufferedReader brTemplate = new BufferedReader(new InputStreamReader(new FileInputStream(new File(BinDir + "template-count.txt")), "UTF8"));
        String line = "";
        while ((line = brRoot.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                hmRoot.put(parts[0], Double.parseDouble(parts[1]));
            }
        }

        while ((line = brTemplate.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                int len = parts[0].length();
                if (!Templates.containsKey(len)) {
                    Templates.put(len, new ArrayList<String>());
                }
                Templates.get(len).add(parts[0]);
                if (!hmTemplate.containsKey(parts[0])) {
                    hmTemplate.put(parts[0], Double.parseDouble(parts[1]));
                }
            }
        }

        for (String fix : ArabicUtils.prefixes)
            affixes.add(fix);
        for (String fix : ArabicUtils.suffixes)
            affixes.add(fix);
        
    }
    
    public String fitTemplate(String word, String pos) {
        pos = pos.replaceFirst("NSUFF.*", "NSUFF");
        if (pos.endsWith("NSUFF")) {
//            word = word.replaceAll("(\u0647|\u0647\u0627|\u0643|\u064a|\u0647\u0645\u0627|\u0643\u0645\u0627|\u0646\u0627|\u0643\u0645|\u0647\u0645|\u0647\u0646|\u0643\u0646|\u0627|\u0627\u0646|\u064a\u0646|\u0648\u0646|\u0648\u0627|\u0627\u062a|\u062a|\u0646|\u0629)$", "");
            word = word.replaceAll("(\u0647|\u0647\u0627|\u0643|\u064a|\u0647\u0645\u0627|\u0643\u0645\u0627|\u0646\u0627|\u0643\u0645|\u0647\u0645|\u0647\u0646|\u0643\u0646|\u0627|\u0627\u0646|\u064a\u0646|\u0648\u0646|\u0648\u0627|\u0627\u062a|\u062a|\u0646)$", "");
        } else if (pos.endsWith("CASE") && word.endsWith("\u0627")) {
            word = word.substring(0, word.length() - 1);
        }
        if (pos.startsWith("DET")) {
            word = word.substring(2);
        }
        String template = fitTemplate(word);
        if (template.equals("Y") && word.endsWith("ت"))
            template = fitTemplate(word.substring(0, word.length() - 1) + "ة");
        return fitTemplate(word);
    }
    
    public String fitTemplate(String line) {
        String tmp = fitStemTemplate(utf82buck(line));
        if (tmp.contains("Y") && (line.endsWith("\u0629") || line.endsWith("\u064a"))) { // ends with ta marbouta or alef maqsoura
            tmp = fitStemTemplate(utf82buck(line.substring(0, line.length() - 1)));
        }
        if (tmp.contains("Y") && (line.endsWith("\u064a\u0629"))) { // ends with ya + ta marbouta
            tmp = fitStemTemplate(utf82buck(line.substring(0, line.length() - 2)));
        }
        if (tmp.contains("Y") && (line.endsWith("\u0649"))) { // ends with alef maqsoura
            tmp = fitStemTemplate(utf82buck(line.substring(0, line.length() - 1) + "\u064a"));
        }
        if (tmp.contains("Y") && (line.contains("\u0623") || line.contains("\u0622") || line.contains("\u0625"))) { // contains any form of alef
            tmp = fitStemTemplate(utf82buck(line.replaceAll("[\u0625\u0623\u0622]", "\u0627"))); // normalize alef
        }
        if (tmp.contains("Y") && line.length() > 1) {
            tmp = fitStemTemplate(utf82buck(line + line.substring(line.length() - 1)));
        }
        if (tmp.contains("Y") && line.startsWith("ات")) {
            tmp = fitStemTemplate(utf82buck(line.substring(0, 1) + "و" + line.substring(1)));
        }
        if (tmp.contains("Y") && line.length() >= 5 && (line.substring(2,3).equals("ط") || line.substring(2,3).equals("د"))) {
            String potential = fitStemTemplate(utf82buck(line.substring(0, 2) + "ت" + line.substring(3)));
            if (potential.length() > 3 && potential.substring(2,3).equals("t"))
                tmp = potential;
        }
        if (tmp.contains("Y") && line.contains("آ"))
        {
            tmp = fitStemTemplate(utf82buck(line.replace("آ", "أا")));
        }
        if (tmp.contains("Y") && (line.contains("ئ") || line.contains("ؤ")))
        {
            tmp = fitStemTemplate(utf82buck(line.replace("ئ", "ء").replace("ؤ", "ء")));
        }
        return tmp;
    }

    private String fitStemTemplate(String stem) {
        ArrayList<String> template = new ArrayList<String>();
        int len = stem.length();
        if (!Templates.containsKey(len)) {
            template.add("Y");
            return "Y";
        } else {
            if (len == 2) {
                if (hmRoot.containsKey(buck2morph(stem + stem.substring(1)))) {
                    template.add("fE");
                    return "fE";
                }
            } else {
                ArrayList<String> t = Templates.get(len);
                for (String s : t) {
                    String root = "";
                    int lastF = -1;
                    int lastL = -1;
                    boolean broken = false;
                    for (int i = 0; i < s.length() && broken == false; i++) {
                        if (s.substring(i, i + 1).equals("f")) {
                            root += stem.substring(i, i + 1);
                        } else if (s.substring(i, i + 1).equals("E")) {
                            // check if repeated letter in the root
                            if (lastF == -1) // letter not repeated
                            {
                                root += stem.substring(i, i + 1);
                                lastF = i;
                            } else // letter repeated
                            {
                                if (stem.substring(i, i + 1) != stem.substring(lastF, lastF + 1)) {
                                    // stem template is broken
                                    broken = true;
                                }
                            }
                        } else if (s.substring(i, i + 1).equals("l")) {
                            // check if repeated letter in the root
                            if (lastL == -1) // letter not repeated
                            {
                                root += stem.substring(i, i + 1);
                                lastL = i;
                            } else // letter repeated
                            {
                                if (stem.substring(i, i + 1) != stem.substring(lastL, lastL + 1)) {
                                    // stem template is broken
                                    broken = true;
                                }
                            }
                        } else if (s.substring(i, i + 1).equals("C")) {
                            root += stem.substring(i, i + 1);
                        } else {
                            if (!stem.substring(i, i + 1).equals(s.substring(i, i + 1))) {
                                // template is broken
                                broken = true;
                            }
                        }
                    }

                    root = ArabicUtils.buck2morph(root);

                    ArrayList<String> altRoot = new ArrayList<String>();
                    if (broken == false && !hmRoot.containsKey(root)) {

                        for (int j = 0; j < root.length(); j++) {
                            if (root.substring(j, j + 1).equals("y") || root.substring(j, j + 1).equals("A") || root.substring(j, j + 1).equals("w")) {
                                String head = root.substring(0, j);
                                String tail = root.substring(j + 1);
                                if (hmRoot.containsKey(head + "w" + tail)) {
                                    altRoot.add(head + "w" + tail);
                                }
                                if (hmRoot.containsKey(head + "y" + tail)) {
                                    altRoot.add(head + "y" + tail);
                                }
                                if (hmRoot.containsKey(head + "A" + tail)) {
                                    altRoot.add(head + "A" + tail);
                                }
                            }
                        }
                    }
                    if (broken == false && hmRoot.containsKey(root)) {
                        template.add(s + "/" + root);
                    }
                    for (String ss : altRoot) {
                        template.add(s + "/" + ss);
                    }
                }
            }

        }
        if (template.size() == 0) {
            template.add("Y");
            return "Y";
        }

        ArrayList<String> templateWithC = new ArrayList<String>();
        ArrayList<String> templateWithoutC = new ArrayList<String>();

        for (String ss : template) {
            if (ss.contains("C")) {
                templateWithC.add(ss);
            } else {
                templateWithoutC.add(ss);
            }
        }
        if (templateWithoutC.size() == 0) {
            return getBestTemplate(template);
        } else {
            return getBestTemplate(templateWithoutC);
        }
    }

    private String getBestTemplate(ArrayList<String> template) {
        double bestScore = 0;
        String bestTemplate = "";
        for (String s : template) {
            String[] parts = s.split("/");
            if (parts.length == 2) {
                double score = hmRoot.get(parts[1]) * hmTemplate.get(parts[0]);
                if (bestScore < score) {
                    bestScore = score;
                    bestTemplate = parts[0];
                }
            }
        }
        return bestTemplate;
    }
    
}
