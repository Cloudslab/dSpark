package core;

import java.io.File;

public class InputDirectorySize {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println((double)inputSize()/1024/1024);

	}

	public static long inputSize() {
	    long length = 0;
	    File inputDirectory= new File("/home/tawfiq/sp/spark-2.0.1/myinput");
	    for (File file : inputDirectory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += inputSize();
	    }
	    return length;
	}
}
