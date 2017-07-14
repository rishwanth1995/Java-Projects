package com.example.FileSearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileSearchApp {

	String path;
	String regex;
	String zipFileName;
	Pattern pattern;
	List<File> zipFiles = new ArrayList<File>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		FileSearchApp app = new FileSearchApp();
		
		switch (Math.min(args.length, 3)) {
		case 0:
			System.out.println("USAGE: FileSearchApp path [regex] [zipfile]");
			return;
		case 3:
			app.setZipFileName(args[2]);
		case 2:
			app.setRegex(args[1]);
		case 1:
			app.setPath(args[0]);
		
		
		}
		try{
			app.walkDirectory(app.getPath());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void walkDirectory(String path) throws IOException {
		// TODO Auto-generated method stub
		Files.walk(Paths.get(path)).forEach( f -> processFile(f.toFile()));
		createZipFile();
	}

	private void createZipFile() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		try(
			 ZipOutputStream out = new ZipOutputStream(new FileOutputStream(this.getZipFileName()))){
			File baseDir = new File(this.getPath());
			
			for( File file : this.zipFiles){
				String fileName = getRelativeFileName(file, baseDir);
				System.out.println(fileName);
				ZipEntry zipEntry = new ZipEntry(fileName);
				zipEntry.setTime(file.lastModified());
				out.putNextEntry(zipEntry);
				
				Files.copy(file.toPath(), out);
				
				out.closeEntry();
			}
		}
	}

	private String getRelativeFileName(File file, File baseDir) {
		// TODO Auto-generated method stub
		String fileName = file.getAbsolutePath().substring(baseDir.getAbsolutePath().length());
		System.out.println(fileName);
		fileName = fileName.replace('\\', '/');
		
		while(fileName.startsWith("/")){
			fileName.substring(1);
		}
		
		return fileName;
	}

	private void processFile(File file) {
		// TODO Auto-generated method stub
		
			try {
				if(searchFile(file)){
					addFileToZip(file);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(file + ":" + e);
			}
		
	}

	private void addFileToZip(File file) {
		// TODO Auto-generated method stub
		if(this.getZipFileName() != null){
			zipFiles.add(file);
		}
	}

	private boolean searchFile(File file) throws IOException {
		// TODO Auto-generated method stub
		return Files.lines(file.toPath(), StandardCharsets.UTF_8).anyMatch(t -> searchText(t));
		
	
	}

	private boolean searchText(String t) {
		// TODO Auto-generated method stub
		return (this.getRegex() == null) ? true : this.pattern.matcher(t).matches();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}

	public String getZipFileName() {
		return zipFileName;
	}

	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

}
