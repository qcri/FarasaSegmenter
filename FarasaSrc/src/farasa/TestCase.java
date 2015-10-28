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
        int args_flag = 0; // correct set of arguments
        
        String dataDirectory = System.getenv("FarasaDataDir");
            if (dataDirectory == null) {
                System.out.println("\"FarasaDataDir\" Environment variable is not set");
                System.exit(-1);
            } 
                
        while (i < args.length) {
            arg = args[i++];
            // 
            if (arg.equals("--help") || arg.equals("-h") || (args.length!=0 && args.length!=4)) {
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
       
        }

        System.err.print("Initializing the system ....");

        Farasa nbt = new Farasa(dataDirectory);
        //processFile("/work/test.txt", nbt);

        System.err.print("\r");
        System.err.println("System ready!               ");
        if(args_flag==0) {
           processFile(nbt);
        }else {
           processFile(infile, outfile, nbt);
        }
    }   


    public static void mainOrig(String[] args) throws UnsupportedEncodingException, IOException, FileNotFoundException, ClassNotFoundException, InterruptedException {
        String inputDir = "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/FarasaData/";

        Farasa nbt = new Farasa(inputDir);
        processFile("/work/test.txt","/work/test.txt.out", nbt);
    }
        
     private static void processFile(Farasa nbt) throws FileNotFoundException, IOException {
         
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        processBuffer(br,bw,nbt);
     }
     
    private static void processFile(String filename, String outfilename, Farasa nbt) throws FileNotFoundException, IOException {
        BufferedReader br = openFileForReading(filename);
        BufferedWriter bw = openFileForWriting(outfilename);
        processBuffer(br,bw,nbt);
    }
     
    private static void processBuffer(BufferedReader br, BufferedWriter bw, Farasa nbt) throws FileNotFoundException, IOException {

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
