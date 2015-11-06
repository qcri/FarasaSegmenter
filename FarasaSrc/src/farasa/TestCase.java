/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package farasa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author kareemdarwish
 */
public class TestCase {
    
      public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException, Exception {  

        
        int i=0;
        String arg;
        String infile="";
        String outfile="";
        String scheme="";
        int args_flag = 0; // correct set of arguments
        
        String dataDirectory = System.getenv("FarasaDataDir");
            if (dataDirectory == null) {
                System.out.println("\"FarasaDataDir\" Environment variable is not set");
                System.exit(-1);
            } 
                
        while (i < args.length) {
            arg = args[i++];
            // 
            if (arg.equals("--help") || arg.equals("-h") || (args.length!=0 && args.length!=2 && args.length!=4 && args.length!=6)) {
                System.out.println("Usage: Farasa <--help|-h> <[-i|--input] [in-filename]> <[-o|--output] [out-filename]>");
                System.exit(-1);
                } 
            
            if (arg.equals("--input") || arg.equals("-i")) {
                                args_flag++;
                                infile = args[i];
                }
            if (arg.equals("--output") || arg.equals("-o")) {
                                args_flag++;
                                outfile = args[i];
                }       
            if (arg.equals("--scheme") || arg.equals("-c")) {
                                args_flag++;
                                scheme = args[i];
                                //System.out.println("Scheme Value:\""+scheme+"\"");
                }            
        }

        System.err.print("Initializing the system ....");

        Farasa nbt = new Farasa(dataDirectory);
        //processFile("/work/test.txt", nbt);

        System.err.print("\r");
        System.err.println("System ready!               ");
        if(args_flag==0) {
           processFile(nbt,scheme);
        }else {
           processFile(infile, outfile, nbt,scheme);
        }
    }   


    public static void mainOrig(String[] args) throws UnsupportedEncodingException, IOException, FileNotFoundException, ClassNotFoundException, InterruptedException {
        String inputDir = "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/FarasaData/";

        Farasa nbt = new Farasa(inputDir);
        processFile("/work/test.txt","/work/test.txt.out", nbt,"");
    }
        
     private static void processFile(Farasa nbt, String sch) throws FileNotFoundException, IOException {
         
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        processBuffer(br,bw,nbt,sch);
     }
     
    private static void processFile(String filename, String outfilename, Farasa nbt, String sch) throws FileNotFoundException, IOException {
        BufferedReader br;
        BufferedWriter bw;
        
        if(!filename.equals(""))
            br = openFileForReading(filename);
        else
            br = new BufferedReader(new InputStreamReader(System.in));
        
        if(!outfilename.equals(""))
            bw = openFileForWriting(outfilename);
        else
            bw = new BufferedWriter(new OutputStreamWriter(System.out));
        
        processBuffer(br,bw,nbt,sch);
    }

private static void processBuffer(BufferedReader br, BufferedWriter bw, Farasa nbt, String sch) throws FileNotFoundException, IOException {

        String line = "";
        String topSolution;
        // HashMap<String, String> seenBefore = new HashMap<String, String>();

        while ((line = br.readLine()) != null) {
            ArrayList<String> words = ArabicUtils.tokenize(ArabicUtils.removeDiacritics(line));
            for (String w : words) {
                if (!nbt.hmSeenBefore.containsKey(w)) {
                    TreeMap<Double, String> solutions = nbt.mostLikelyPartition(ArabicUtils.buck2utf8(w), 1);
                    topSolution = w;
                    if (solutions.size() > 0)
                        topSolution = solutions.get(solutions.firstKey());
                    topSolution = topSolution.replace(";", "").replace("++", "+");
                    nbt.hmSeenBefore.put(w, topSolution);
                    
                    if(sch.equals("atb")) {
                        topSolution = produceSpecialSegmentation(topSolution, nbt);
                    }
                    bw.write(topSolution.replace(";", "").replace("++", "+") + " ");
                    bw.flush();
                    
                }
                else
                {
                    if(sch.equals("atb")) {
                        topSolution = produceSpecialSegmentation(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt);
                    }
                    else 
                        topSolution = nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+") + " ";
                    
                    bw.write(topSolution + " ");
                }
            }
            bw.write("\n");
        }
        bw.close();
    }    
    
private static void processBufferNew(BufferedReader br, BufferedWriter bw, Farasa nbt) throws FileNotFoundException, IOException {

        String line = "";
        
        // HashMap<String, String> seenBefore = new HashMap<String, String>();
        
        while ((line = br.readLine()) != null) {
            ArrayList<String> words = ArabicUtils.tokenize(ArabicUtils.removeDiacritics(line));
            for (String w : words) {
                if (!nbt.hmSeenBefore.containsKey(w)) {
                    TreeMap<Double, String> solutions = nbt.mostLikelyPartition(ArabicUtils.buck2utf8(w), 1);
                    String topSolution = w;
                    if (solutions.size() > 0)
                        topSolution = solutions.get(solutions.firstKey());
                    topSolution = topSolution.replace(";", "").replace("++", "+");
                    nbt.hmSeenBefore.put(w, topSolution);
                    
                    topSolution = produceSpecialSegmentation(topSolution, nbt);
                    
                    bw.write(topSolution.replace(";", "").replace("++", "+") + " ");
                    bw.flush();
                    
                }
                else
                {
                    String topSolution = produceSpecialSegmentation(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt);
                    bw.write(topSolution + " ");
                }
            }
            bw.write("\n");
        }
        bw.close();
    }
    
    public static String produceSpecialSegmentation(String segmentedWord, Farasa nbt)
    {
        String output = "";
        
        String tmp = nbt.getProperSegmentation(segmentedWord);
        
        // attach Al to the word
        tmp = tmp.replace("ال+;", ";ال");
        
        // attach ta marbouta
        tmp = tmp.replace(";+ة", "ة;");
        
        // concat all prefixes and all suffixes
        String[] parts = (" " + tmp + " ").split(";");
        
        // handle prefix
        tmp = parts[0].replace("+", "").trim();
        if (tmp.length() > 0)
            output += tmp + "+ ";
        
        // handle stem
        output += parts[1].trim();
        
        // handle suffix
        tmp = parts[2].replace("+", "").trim();
        if (tmp.length() > 0)
            output += " +" + tmp;
        
        output = output.trim();
        while (output.startsWith("+"))
            output = output.substring(1);
        while (output.endsWith("+"))
            output = output.substring(0, output.length() - 1);
        
        return output;
    }
       
    public static String produceSpecialSegmentation0(String segmentedWord, Farasa nbt)
    {
        String output = "";
        
        String tmp = nbt.getProperSegmentation(segmentedWord);
        
        // attach Al to the word
        tmp = tmp.replace("ال+;", ";ال");
        
        // concat all prefixes and all suffixes
        String[] parts = (" " + tmp + " ").split(";");
        
        // handle prefix
        tmp = parts[0].replace("+", "").trim();
        if (tmp.length() > 0)
            output += tmp + "+ ";
        
        // handle stem
        output += parts[1].trim();
        
        // handle suffix
        tmp = parts[2].replace("+", "").trim();
        if (tmp.length() > 0)
            output += " +" + tmp;
        
        output = output.trim();
        while (output.startsWith("+"))
            output = output.substring(1);
        while (output.endsWith("+"))
            output = output.substring(0, output.length() - 1);
        
        return output;
    }
 
    private static void processBuffer0(BufferedReader br, BufferedWriter bw, Farasa nbt) throws FileNotFoundException, IOException {

        String line = "";
        
        // HashMap<String, String> seenBefore = new HashMap<String, String>();
        
        while ((line = br.readLine()) != null) {
            ArrayList<String> words = ArabicUtils.tokenize(ArabicUtils.removeDiacritics(line));
            for (String w : words) {
                if (!nbt.hmSeenBefore.containsKey(w)) {
                    TreeMap<Double, String> solutions = nbt.mostLikelyPartition(ArabicUtils.buck2utf8(w), 1);
                    String topSolution = w;
                    if (solutions.size() > 0)
                        topSolution = solutions.get(solutions.firstKey());
                    bw.write(topSolution.replace(";", "").replace("++", "+") + " ");
                    bw.flush();
                    nbt.hmSeenBefore.put(w, topSolution);
                }
                else
                {
                    bw.write(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+") + " ");
                }
            }
            bw.write("\n");
        }
        bw.close();
    }
           
    public static BufferedReader openFileForReading(String filename) throws FileNotFoundException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        return sr;
    }

    public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
        BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
        return sw;
    }    
}
