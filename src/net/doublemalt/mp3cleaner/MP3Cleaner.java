package net.doublemalt.mp3cleaner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.myid3.MyID3;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;


public class MP3Cleaner {

	private static final MyID3 MY_ID3 = new MyID3();

	@Parameter(names = {"--help", "-h"}, description="Displays this message", help = true)
	private boolean help = false;

	@Parameter
	private List<String> parameters = new ArrayList<String>();

	@Parameter(names = { "--name", "-n" }, description = "Selects all files that contain the string in the name", required=true)
	private String name;

	@Parameter(names = { "-o", "-or" }, description = "Deletes all files where any of the conditions is true")
	private boolean or = false;

	@Parameter(names = { "--pretend", "-p" }, description = "Pretend mode (just prints names of files it would delete)")
	private boolean pretend = false;

	@Parameter(names = { "--debug", "-d" }, description = "Debug mode", hidden = true)
	private boolean debug = false;

	@Parameter(names = { "--verbode", "-v" }, description = "Chatty mode")
	private boolean verbose = false;

	private int deleted = 0;

	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		
		MP3Cleaner cleaner = new MP3Cleaner();

		JCommander jCommander = new JCommander(cleaner, args);
		
		if(cleaner.help)
		{
			jCommander.usage();
		}
		else
		{
			cleaner.walk(new File("."));
			System.out.println("Deleted " + cleaner.deleted + " files.");

		}

	}

	public void walk(File root) throws IOException {

		File[] list = root.listFiles();

		for (File f : list) {
			if (f.isDirectory()) {
				walk(f);
			} else {
				try {
					if(debug)
					{
						System.out.println("Processing " + f.getCanonicalPath());
					}
					
					IMusicMetadata metaData = MY_ID3.read(f).getSimplified();
					String title = metaData.getSongTitle();
					if(title != null && title.toUpperCase().contains(this.name.toUpperCase()))
					{
						deleted++;
						if(verbose || pretend || debug)
						{
							System.out.println("Deleting " + f.getCanonicalPath() + " with title " + title);
						}
						if(!pretend)
						{
							f.delete();
						}
					}
					
				} catch (Exception e) {
					if(verbose || debug)
					{
						System.out.println("Could not read tags from " + f.getCanonicalPath());
					}
					if(debug)
					{
						System.out.println("Error: " + e.getMessage());
					}
				}
			}
		}
	}

}
