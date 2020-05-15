import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.github.javaparser.ParseException;
import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;

public class graph_diff {
	public static void main(String[] args) throws Exception {
		File OringinalFile = new File("C:\\Users\\nvuggam\\Desktop\\Original.java");
		File RevisedFile = new File("C:\\Users\\nvuggam\\Desktop\\Revised.java");
		final Diff result = new AstComparator().compare(OringinalFile, RevisedFile);
		File diff = new File("C:\\Users\\nvuggam\\Desktop\\Difference.txt");
		PrintStream he = new PrintStream(diff);
		System.setOut(he);
		System.out.println(result.toString());
		
		File MethodLineInfo= new File("C:\\Users\\nvuggam\\Desktop\\Mline.txt");
    	PrintStream hi = new PrintStream(MethodLineInfo);
    	System.setOut(hi);
		getMethodLineNumbers(OringinalFile);

		Vector<Integer> StartLineNumbers = new Vector<Integer>();
		Vector<Integer> EndLineNumbers =new Vector<Integer>();
		Vector<String> MethodNames = new Vector<String>();
		BufferedReader Mr = new BufferedReader(new FileReader(MethodLineInfo));
		String mline = null;
		while(  (mline = Mr.readLine()) != null )
		{
			String[] WordsInLine = mline.split(" ",3);
			int StarT =  Integer.parseInt(WordsInLine[0]);
			int EnD = Integer.parseInt(WordsInLine[1]);
			String MNaMe = WordsInLine[2];
			StartLineNumbers.add(StarT);
			EndLineNumbers.add(EnD);
			MethodNames.add(MNaMe);
		}
		Mr.close();
		PrintStream Console = System.out;
		System.setOut(Console);
		BufferedReader br = new BufferedReader(new FileReader(diff));  
		String line = null;  
		StringBuffer inputBuffer = new StringBuffer();
		while ((line = br.readLine()) != null)  
		{  
			System.out.println(line);
		     String [] WordsOfLine = line.split(" ",3);
		     if(WordsOfLine[0].equals("Move"))
		     {
		    	 if(WordsOfLine[1].equals("Method") )
		    	 {
		    		 line = "";
		    	 }
		    	 else
		    	 {
		    		 String ExtractLineNumber = extractInt(line);
		    		 if(!ExtractLineNumber.equals("-1"))
		    		 {
		    			 String[] WordsInLine = ExtractLineNumber.split(" ");
		    			 
		    			 int num1 = Integer.parseInt(WordsInLine[0]);
		    			 int LengthWordsInLine = WordsInLine.length;
		    			 
		    			 int l1 = binarySearch(StartLineNumbers,0,StartLineNumbers.size()-1,num1);
		    			 line = "";
		    			 if(l1>=0) 
		    			 {
			    			 if(num1 <= EndLineNumbers.get(l1))
			    			 {
			    				 line = line + MethodNames.get(l1);
			    			 } 
		    			 }
		    			 if(LengthWordsInLine>=2)
		    			 {
		    				 int num2 = Integer.parseInt(WordsInLine[1]);
			    			 l1 = binarySearch(StartLineNumbers,0,StartLineNumbers.size()-1,num2);
			    			 if(l1>=0)
			    			 {
				    			 if(num2 <= EndLineNumbers.get(l1))
				    			 {
				    				 line = line + MethodNames.get(l1);
				    			 } 
			    			 }
		    			 }
		    		 }	
		    		 else
		    	 	 {
		    			 line = "";
		    	 	 } 
		    	 }
		     }
		     else if( WordsOfLine[0].equals("Update") || WordsOfLine[0].equals("Insert") || WordsOfLine[0].equals("Delete") )
		     {
		    	 if(! WordsOfLine[1].equals("Method") )
		    	 {
		    		 String ExtractLineNumber = extractInt(line);
		    		 if(!ExtractLineNumber.equals("-1"))
		    		 {
		    			 int LineNumberInRevisedFile = Integer.parseInt(ExtractLineNumber);
		    			 int l1 = binarySearch(StartLineNumbers,0,StartLineNumbers.size()-1,LineNumberInRevisedFile);
		    			 if(l1>=0)
		    			 {
			    			 if(LineNumberInRevisedFile <= EndLineNumbers.get(l1))
			    			 {
			    				 line = "";
			    				 line = line + MethodNames.get(l1);
			    			 }
			    			 else line = "";
		    			 }
		    			 else line = "";
		    		 }
		    		 else
		    		 {
		    			 line = "";
		    		 }	
		    	 }
		    	 else
		    	 {
		    		 line = "";
		    	 }
		     } 
		    inputBuffer.append(line);
	        inputBuffer.append('\n');
		}
		br.close();
		FileOutputStream fileOut = new FileOutputStream("C:\\Users\\nvuggam\\Desktop\\Difference.txt");
		fileOut.write(inputBuffer.toString().getBytes()); 
		fileOut.close();
		Tokenize t = new Tokenize();
		t.main(null);
		}
		private static String extractInt(String str) {
			str = str.replaceAll("[^\\d]", " "); 
            str = str.trim(); 
            str = str.replaceAll(" +", " "); 
            if (str.equals("")) 
                return "-1"; 
            return str;
		}
		private static int binarySearch(Vector<Integer> arr, int l, int r, int x) 
	    { 
	        if (r >= l) { 
	            int mid = l + (r - l) / 2; 
	            if (arr.get(mid) == x) 
	                return mid; 
	            if (arr.get(mid) > x) 
	                return binarySearch(arr, l, mid - 1, x); 
	            return binarySearch(arr, mid + 1, r, x); 
	        } 
	        return r; 
	    }
		private static void getMethodLineNumbers(File src) throws ParseException, IOException {
	        CompilationUnit cu = StaticJavaParser.parse(src);
	        new MethodVisitor().visit(cu, null);
	    }
		
	    private static class MethodVisitor extends VoidVisitorAdapter {
	        @Override	    
	        public void visit(MethodDeclaration m, Object arg) {
	        	int start = m.getRange().get().begin.line;
	        	int end = m.getRange().get().end.line;
	        	String MName = m.getName().toString();
	        	System.out.println(m.getRange().get().begin.line + " "  + m.getRange().get().end.line + " " + m.getName());            
	    }
	        
	    }	
}
