package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.swing.JTextArea;

import window.Window;

/**
 *
 *  This file is part of Spatcher.
 *
 *  Spantcher is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Spatcher is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Spatcher.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  @author 3lm4dn0
 * 
 */
public class Spatcher {

	/* for use in window mode */
	private static JTextArea textArea;

	private final static String title = "Parche de traducción para Snatcher de Sega Mega CD";
	private final static String version = "0.98c";

	public static enum IsoVersion {
		PAL, NTCS
	};

	private static boolean textModeActive = false;

	private static String author = "david_82";
	private static String translatorTeam = "\tJoxer (Creador del proyecto y traductor)\n"
			+ "\tdavid_82 (programador y traductor)\n"
			+ "\tMairtrus (programador y traductor)\n"
			+ "\tDrasglaf (traductor)\n"
			+ "\tjam007 (traductor)\n"
			+ "\tManveru Ainu (traductor)\n"
			+ "\trandam_hajile (traductor)"
			+ "\ttonycc (traductor)\n"
			+ "\tdjnox (traductor)\n"
			+ "\tlivekraft (traductor)\n" + "\tTolvatar (traductor)\n";

	private static String thanks = ""
			+ "\tJoxer por la iniciativa de realizar este trabajo en grupo.\n"
			+ "\tMaitrus porque sin sus conocimientos técnicos de romhacking e ingeniería inversa no habría sido posible realizar esta traducción.\n"
			+ "\tTodos los traductores, algunos habeis traducido más que otros, pero en general todos habeis hecho posible crear esta traducción.\n"
			+ "\t@LuisPCastelo por ayudar a corregir frases correctamente al español.\n";

	private static String website = "http://www.elotrolado.net/hilo_proyecto-snatcher-en-espanol_1790142";
	private static String project = "https://github.com/3lm4dn0/Sega-CD-Snatcher-Language-Patcher";

	/**************************************/

	/**
	 * Paths the iso from a given a sequence of bytes and adds a new version of
	 * SUBCODE file (PAL|NTSC)
	 * 
	 * @param bs
	 * @param version
	 * @param censured
	 * @return
	 * @throws IOException
	 */
	public static byte[] patchIso(byte[] binaryDataSource, IsoVersion version,
			boolean removeCensure) throws IOException {

		List<String> binaryFiles = getBinaryFilesList(removeCensure);

		// Files Table from 0xA000 (40960) to 0x0B26A (45674)
		byte[] filesTable = ByteUtils.subByte(binaryDataSource, 40960,
				45674 - 40960);

		// Read files
		ZipInputStream zip = FileUtils.readZip();

		// Replace each binary file
		for (String file : binaryFiles) {
			patchFile(binaryDataSource, file, file, filesTable, zip);
		}

		// Replace the subcode file
		if (version.equals(IsoVersion.PAL)) {
			patchFile(binaryDataSource, "SUBCODE_E.BIN", "SUBCODE.BIN",
					filesTable, zip);
		} else if (version.equals(IsoVersion.NTCS)) {
			patchFile(binaryDataSource, "SUBCODE_U.BIN", "SUBCODE.BIN",
					filesTable, zip);
		}

		// Replace files table in binaryDataSource
		ByteUtils.replaceFile(binaryDataSource, filesTable, 40960,
				45674 - 40960);

		return binaryDataSource;
	}

	/**
	 * 
	 * Create an array with all file names to replace in the iso game
	 * 
	 * NOTE: Not change the order from binary files!
	 * */
	private static List<String> getBinaryFilesList(boolean removeCensure) {

		/*
		 * DATA_D1.BIN: Introduction game DATA_G0.BIN: Diary disk PC89
		 * DATA_P0.BIN: Uncensured Katrina at bathroom
		 */
		List<String> binaryFiles = new ArrayList<String>();

		binaryFiles.addAll(Arrays.asList("DATA_D0.BIN", "DATA_D1.BIN",
				"DATA_D2.BIN", "DATA_F0.BIN", "DATA_G0.BIN", "DATA_H04.BIN",
				"DATA_H09.BIN", "DATA_H15.BIN", "DATA_M0.BIN", "DATA_O0.BIN",
				"DATA_Y16.BIN"));

		if (removeCensure) {
			// Set in order
			binaryFiles.add(10, "DATA_P0.BIN");
		}

		binaryFiles.addAll(Arrays.asList("SP00.BIN", "SP01.BIN", "SP02.BIN",
				"SP03.BIN", "SP04.BIN", "SP05.BIN", "SP06.BIN", "SP07.BIN",
				"SP08.BIN", "SP09.BIN", "SP10.BIN", "SP11.BIN", "SP12.BIN",
				"SP13.BIN", "SP14.BIN", "SP15.BIN", "SP16.BIN", "SP17.BIN",
				"SP18.BIN", "SP19.BIN", "SP20.BIN", "SP21.BIN", "SP22.BIN",
				"SP23.BIN", "SP24.BIN", "SP25.BIN", "SP26.BIN", "SP27.BIN",
				"SP28.BIN", "SP29.BIN", "SP30.BIN", "SP32.BIN", "SP33.BIN",
				"SP34.BIN", "SP35.BIN", "SP36.BIN", "SP37.BIN", "SP38.BIN"));

		return binaryFiles;
	}

	/**
	 * Replace one file
	 * 
	 * @param bs
	 *            original ISO code
	 * @param srcFile
	 *            source from ISO file
	 * @param dstFile
	 *            destination ISO
	 * @param filesTable
	 *            table from files in ISO
	 * @param zip
	 *            file zipped
	 * @throws IOException
	 */
	private static void patchFile(byte[] bs, String srcFile, String dstFile,
			byte[] filesTable, ZipInputStream zip) throws IOException {
		int pos, oriFileSize, offset;
		byte[] sector_LE, size_LE, bfile;

		// Get the position from file
		pos = ByteUtils.indexOf(filesTable, dstFile.getBytes());

		// Get data from table file LittleEndian
		sector_LE = ByteUtils.subByte(filesTable, pos - 27, 4);
		size_LE = ByteUtils.subByte(filesTable, pos - 19, 4);

		// Get original filesize from Files Table
		oriFileSize = (int) Long.parseLong(ByteUtils.toHexString(size_LE), 16);

		// Show data
		String infoFile = new String("Patching " + dstFile + "...");
		if (textModeActive) {
			System.out.print(infoFile);
		} else {
			textArea.append("\r\n" + infoFile);
		}

		// Get original offset from binary file
		offset = (int) Long.parseLong(ByteUtils.toHexString(sector_LE), 16) * 2048;

		// Read file from resources
		bfile = FileUtils.readResourceZip(srcFile, zip);

		// Show file size
		String infoSize = " (original filesize " + oriFileSize
				+ " / new filesize " + bfile.length + ")";
		if (textModeActive) {
			System.out.println(infoSize);
		} else {
			textArea.append("\r\n" + infoSize);
		}

		// Replaces data from bs with bfile in sector_LE and length oriFileSize
		ByteUtils.replaceFile(bs, bfile, offset, oriFileSize);

		// Replace table
		if (bfile.length != oriFileSize) {
			byte[] newFileSize = ByteUtils.toByteArray(String.format("%08X",
					ByteUtils.little2big(bfile.length) & 0xFFFFFFFF)
					+ String.format("%08X", bfile.length & 0xFFFFFFFF));

			ByteUtils.replaceFile(filesTable, newFileSize, pos - 23, 8);
		}
	}

	/**
	 * Get Version
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static IsoVersion checkVersion(String filename) throws IOException {
		if (textModeActive) {
			System.out.print("Checking iso " + filename + "... ");
		}

		byte[] fileData = FileUtils.subStr(filename, 0, 256);

		String pal = "534547414449534353595354454D20205345474149504D454E552000010000014B4F4E414D492053303032000001000000000800000060000000000000000000000068000000180000000000000000003130313231393934202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020";
		String usa = "534547414449534353595354454D20205345474149504D454E552000010000014B4F4E414D492052303032000001000000000800000060000000000000000000000068000000180000000000000000003130313031393934202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020";

		if (ByteUtils.toHexString(fileData).equalsIgnoreCase(pal)) {
			return IsoVersion.PAL;
		}

		if (ByteUtils.toHexString(fileData).equalsIgnoreCase(usa)) {
			return IsoVersion.NTCS;
		}

		String error = filename + " is not a sega cd snatcher iso.";
		throw new IOException(error);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Text mode
		if ((args.length > 0) && args[0].equals("-T")) {
			textModeActive = true;
			textMode(args);
			return;
		}

		// Window mode
		new Window(title, version, author, translatorTeam, thanks, project,
				website);
	}

	private static void textMode(String[] args) {
		boolean removeCensure = false;
		String removeCensureText = "--removeCensure";
		String removeCensureFlag = "-r";

		if (args.length < 3) {
			System.out.println(title);
			System.out.println("Version " + version);
			System.out.println();
			System.out
					.println("Usage: java -jar spatcher.jar [-T] original.iso patched.iso ["
							+ removeCensureText
							+ " | "
							+ removeCensureFlag
							+ "]\n");
			System.out.println();

			System.out.println("Autor del parcheador: " + author);
			System.out.println();
			System.out.println("Equipo de traducción:\n" + translatorTeam);
			System.out.println();
			System.out.println("Agradecimientos a:\n" + thanks);
			System.out.println();
			System.out.println("Foro del proyecto " + website
					+ " para más información");
			System.out.println("Herramienta de traducción " + project);

			return;
		}

		// Patch ISO
		byte[] fileData;
		try {
			String src = args[1];
			String dst = args[2];

			if ( args.length > 3 )
			{
				if ( args[3].equals(removeCensureText) || args[3]
							.equals(removeCensureFlag) )
				{
					removeCensure = true;
					System.out.println("Remove censure flag enabled\n");

				}else{
					System.out.println(String.format("Unrecognized option: %s\n", args[3]));
				}
			}

			IsoVersion iv = checkVersion(src);

			System.out.println(iv + " version.");

			// Patch ISO
			fileData = patchIso(FileUtils.readFile(src), iv, removeCensure);

			// Create new ISO
			FileUtils.createFile(dst, fileData);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set JTextComponent
	 * 
	 * @param jText
	 */
	public static void setTextArea(JTextArea jText) {
		textArea = jText;
	}

}
