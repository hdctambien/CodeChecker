import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

import java.util.*;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;

public class MainWindow
{
    public static String readFile(String path)
    {
      try
      {
        return  new String(Files.readAllBytes(Paths.get(path)));
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      return "";
    }

    public static List<Integer> filterOutliers(List<Integer> data)
    {
      Collections.sort(data);
      int median = getMedian(data);
      int middleIndex = data.size() / 2;
      List<Integer> left = sublist(data, 0, middleIndex);
      List<Integer> right = sublist(data, middleIndex+1, data.size());

      int q1 = getMedian(left);
      int q3 = getMedian(right);

      int iqr = q3 - q1;

      double radius = iqr * 1.5;

      double upper = q3 + radius;
      double lower = q1 - radius;

      List<Integer> ret = new LinkedList<>();

      for(int datum : data)
      {
        if(datum < upper && datum > lower)
        {
          ret.add(datum);
        }
      }

      return ret;
    }

    public static List<Integer> sublist(List<Integer> data, int from, int to)
    {
      List<Integer> ret = new ArrayList<>();

      for(int i=from; i<to; i++)
      {
        ret.add(data.get(i));
      }

      return ret;
    }

    public static double getMean(List<Integer> data)
    {
      double sum = 0;
      for(int datum : data)
      {
        sum += datum;
      }

      return sum/data.size();
    }

    public static int getMedian(List<Integer> data)
    {
      if(data.size() == 0)
        return -1;

      Collections.sort(data);
      int middle = data.size() / 2;
      return data.get(middle);
    }

    public static int getDiff(String a, String b)
    {
        /*
        The Levenshtein distance between two words is the minimum number of single-character edits (insertions, deletions or substitutions) required to change one word into the other.
        This distance is computed as levenshtein distance divided by the length of the longest string. The resulting value is always in the interval [0.0 1.0]
        The similarity is computed as 1 - normalized distance
         */
        NormalizedLevenshtein l = new NormalizedLevenshtein();
        //https://github.com/tdebatty/java-string-similarity#download
        return 100-(int)(100*l.distance(a, b));
    }

    public static double calculateSD(List<Integer> data, int total, double mean)
    {
      // https://www.programiz.com/java-programming/examples/standard-deviation
        double sd = 0.0;

        for(int datum: data) {
            sd += Math.pow(datum - mean, 2);
        }

        return Math.sqrt(sd/total);
    }

    public static int calculateMode(List<Integer> data)
    {
      Map<Integer, Integer> counts = new HashMap<>();

      int biggestCount = 0;
      int biggestCountValue = -1;
      for(int datum : data)
      {
        int count = 1;
        if(counts.containsKey(datum))
        {
          count = counts.get(datum);
        }

        counts.put( datum, count );

        if(count > biggestCount)
        {
          biggestCount = count;
          biggestCountValue = datum;
        }
      }

      return biggestCountValue;
    }
    public static int numInRange(List<Integer> data, int low, int high)
    {
      int count = 0;

      for(int datum : data)
      {
        if(datum >= low && datum <= high)
        {
          count++;
        }
      }

      return count;
    }

    public static void main(String[] args)
    {
      File root = null;
      if(args.length >= 1)
      {
        root = new File(args[0]);
      }
      else
      {
        root = new File(".");
      }

      String targetFilename = null;
      if(args.length >= 2)
      {
        targetFilename = args[1];
      }
      else
      {
          System.out.println("Syntax: java MainWindow path/to/student/folders FileToCompare [stats]");
          return;
      }

      //if anything is passed as the 3rd argument, then output stats instead of raw data
      boolean outputStats = args.length >= 3;


      // store folderName -> javaFile
      Map<String, String> studentFiles = new HashMap<>();
      List<String> studentNames = new ArrayList<>();

      File[] folders = root.listFiles();
      for(File folder : folders)
      {
        if(!folder.isDirectory()) continue;

        String folderName = folder.getName().replaceAll("_", " ");

        String filePath = folder.getPath() + File.separator + targetFilename;
        File targetFile = new File(filePath);

        if(targetFile.exists())
        {
          String data = readFile(targetFile.getPath()).trim();
          if(!"".equals(data))
          {
            studentFiles.put(folderName, data);
            studentNames.add(folderName);
          }
        }
      }

      int total = 0;
      int sum = 0;
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      List<Integer> data = new LinkedList<>();

      if(!outputStats) System.out.printf("Student A %s, Student B %s, Similarity%n", targetFilename, targetFilename);

      for(int i=0; i<studentNames.size(); i++)
      {
        for(int j=i+1; j<studentNames.size(); j++)
        {
          String studentA = studentNames.get(i);
          String studentB = studentNames.get(j);

          String codeA = studentFiles.get(studentA);
          String codeB = studentFiles.get(studentB);

          int diff = getDiff(codeA, codeB);

          if(!outputStats)  System.out.printf("%s, %s, %d%n", studentA, studentB, diff);
          if(!outputStats)  System.out.printf("%s, %s, %d%n", studentB, studentA, diff);

          data.add(diff);
          total++;
          sum += diff;

          if(diff < min) min = diff;
          if(diff > max) max = diff;
        }
      }

      if(outputStats)
      {
        if(0 == total)
        {
          System.out.println("No data.");
          return;
        }

        data = filterOutliers(data);

        double mean = getMean(data);
        double sd = calculateSD(data, data.size(), mean);
        int mode = calculateMode(data);
        int median = getMedian(data);

        System.out.printf("Total, %d%n", total);
        System.out.printf("Sum, %d%n", sum);
        System.out.printf("Min, %d%n", min);
        System.out.printf("Max, %d%n", max);
        System.out.printf("Mean, %f%n", mean);
        System.out.printf("Mode, %d%n", mode);
        System.out.printf("Median, %d%n", median);
        System.out.printf("Standard Deviation, %f%n", sd);

        int low = (int)Math.floor(mean - sd);
        int high = (int)Math.ceil(mean + sd);
        int numIn1stSD = numInRange(data, low, high);
        System.out.printf("1SD, %d, %d%n", low, high);

        low = (int)Math.floor(mean - sd * 2);
        high = (int)Math.ceil(mean + sd * 2);
        int numIn2ndSD = numInRange(data, low, high) - numIn1stSD;
        System.out.printf("2SD, %d, %d%n", low, high);

        low = (int)Math.floor(mean - sd * 3);
        high = (int)Math.ceil(mean + sd * 3);
        System.out.printf("3SD, %d, %d%n", low, high);

        int numOutOfBounds = total - numIn2ndSD - numIn1stSD;

        System.out.printf("Count 1SD, %d%n", numIn1stSD);
        System.out.printf("Count 2SD, %d%n", numIn2ndSD);
        System.out.printf("Count 3SD+, %d%n", numOutOfBounds);
      }
    }


}
