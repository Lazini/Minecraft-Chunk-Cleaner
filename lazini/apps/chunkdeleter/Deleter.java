package lazini.apps.chunkdeleter;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import lazini.apps.chunkdeleter.mojang.chunk.storage.RegionFile;
import lazini.apps.chunkdeleter.mojang.nbt.CompoundTag;
import lazini.apps.chunkdeleter.mojang.nbt.ListTag;
import lazini.apps.chunkdeleter.mojang.nbt.StringTag;
import lazini.apps.chunkdeleter.mojang.nbt.Tag;

public class Deleter {

	private Options options;
	private ArrayList<File> files = new ArrayList<>(10000);
	private HashSet<String> safeChunks = new HashSet<>((int) Math.pow(2, 24));
	private ArrayList<String> blockIDsList = new ArrayList<>(100);
	private String corruptedChunks = "Corrupted chunks: ";
	private String corruptedRegionFiles = "Corrupted region files: ";
	private int totalChunksEnqueued = 0;
	private int chunksEnqueued = 0;

	public Deleter(Main main) {
		this.options = main.getOptions();
		totalChunksEnqueued = 0;
	}

	/** This method loads the world's region files to memory. */
	public void loadWorld() {
		String folderName = options.getSaveFolderFile().getParentFile().getName();
		System.out.println("--------Loading world: " + folderName + "--------");
		File filesTemp[] = options.getSaveFolderFile().listFiles();
		for (File file : filesTemp) {
			if (file.getName().endsWith("mca")) {

				files.add(file);
			}
		}
		System.out.println("--------World loaded successfully--------");
	}

	/** This method unloads the world's region files from memory. */
	public void unloadWorld() {
		System.out.println("--------Unloading world--------");
		files.clear();
		System.out.println("--------World unloaded successfully--------");
	}

	/**
	 * This method prepares the chunks for deletion. It adds them all to a
	 * general list that later on is used to determine which chunks will be
	 * deleted and which not.
	 */
	public void enqueue() {
		System.out.println("--------Enqueuing--------");
		chunksEnqueued = 0;

		String folderName = options.getSaveFolderFile().getParentFile().getName();
		int totalFiles = options.getSaveFolderFile().listFiles().length;
		for (Option option : options.getOptionsList()) {
			int counter = 0;
			int minY = option.getMinY();
			int maxY = option.getMaxY();
			int radius = option.getRadius();
			String blockIDs[] = option.getBlockIDs();
			System.out.println("Current settings of enqueueing");
			System.out.println("World file:" + folderName);
			System.out.println("Whitelisted block IDs: " + Arrays.toString(blockIDs));
			System.out.println("Minimum Y: " + minY);
			System.out.println("Maximum Y: " + maxY);
			System.out.println("Radius: " + radius);
			//START

			//Actual enqueuing takes place here
			for (File file : files) {
				counter++;
				System.out.println("Progress: " + counter + "/" + totalFiles + ". Total chunks enqueued: " + totalChunksEnqueued);
				try {
					RegionFile regionFile = new RegionFile(file);

					String temp = file.getName();
					temp = temp.substring(2, temp.length() - 4);
					String mcaName[] = temp.split("\\.");
					int regionX = Integer.valueOf(mcaName[0]);
					int regionZ = Integer.valueOf(mcaName[1]);

					for (int chunkX = 0; chunkX < 32; chunkX++) {
						for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
							if (safeChunks.contains("" + chunkX + "_" + chunkZ + "_" + regionX + "_" + regionZ))
								continue;
							DataInputStream chunk = regionFile.getChunkDataInputStream(chunkX, chunkZ);
							if (regionFile.hasChunk(chunkX, chunkZ)) {
								try {
									Tag root = Tag.readNamedTag(chunk);

									CompoundTag level = root.getCompound("Level");
									ListTag sections = level.getList("Sections");

									outer: for (int i = 0; i < sections.size(); i++) {
										CompoundTag section = (CompoundTag) sections.get(i);
										ListTag palette = section.getList("Palette");
										for (int j = 0; j < palette.size(); j++) {
											CompoundTag blockState = (CompoundTag) palette.get(j);
											StringTag blockName = (StringTag) blockState.get("Name");
											for (String block : blockIDs) {
												if (blockName.data.equalsIgnoreCase(block)) {
													markSafeChunks(regionFile, chunkX, chunkZ, radius, regionX, regionZ);
													break outer;
												}
											}
										}
								}

								} catch (IOException e1) {
									e1.printStackTrace();
								} catch (NullPointerException e2) {
//									e2.printStackTrace();
//									System.exit(-1);
									int locX = (regionX * 32 + chunkX);
									int locZ = (regionZ * 32 + chunkZ);
									System.out.println(
											"\tIMPORTANT INFO: Found corrupted chunk at: " + locX + ", " + locZ + "! Ignoring it!");
									safeChunks.add("" + chunkX + "_" + chunkZ + "_" + regionX + "_" + regionZ);
									chunksEnqueued++;
									totalChunksEnqueued++;
									if (!corruptedChunks.contains(locX + "," + locZ))
										corruptedChunks = corruptedChunks + "(" + locX + "," + locZ + ")" + " | ";
									continue;
								}
							}
						}
					}

					try {
						regionFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (IndexOutOfBoundsException e1) {
					e1.printStackTrace();
					System.out.println("\tIMPORTANT INFO: Could not access correctly region file: " + file.getName() + "! Ignoring it!");
					if (!corruptedRegionFiles.contains(file.getName()))
						corruptedRegionFiles = corruptedRegionFiles + file.getName() + ", ";
					continue;
				}

			}

			//END
			System.out.println("Current settings of enqueueing");
			System.out.println("World file: " + folderName);
			System.out.println("Whitelisted block IDs: " + Arrays.toString(blockIDs));
			System.out.println("Minimum Y: " + minY);
			System.out.println("Maximum Y: " + maxY);
			System.out.println("Radius: " + radius);
			System.out.println(corruptedChunks);
			System.out.println(corruptedRegionFiles);
//			System.out.println("Current memory used in total: " + Runtime.getRuntime().totalMemory());
//			System.out.println("Current memory available (approximately): " + Runtime.getRuntime().freeMemory());
//			System.out.println("Maximum memory which can be allocated: " + Runtime.getRuntime().maxMemory());
//			System.out.println("Available processors count: " + Runtime.getRuntime().availableProcessors());
			System.out.println("\tChunks enqueued this time: " + chunksEnqueued);
			System.out.println("\tTotal chunks enqueued: " + totalChunksEnqueued);
			System.out.println("--------Finished enqueueing--------");
		}
	}

	/**
	 * This method deletes all chunks that have been enqueued and not marked as
	 * safe.
	 */
	public void delete() {
		System.out.println("--------Deleting--------");
		String folderName = options.getSaveFolderFile().getParentFile().getName();

		int totalFiles = options.getSaveFolderFile().listFiles().length;
		int counter = 0;
		int chunksDeleted = 0;
		System.out.println("Current settings of deleting");
		System.out.println("World file:" + folderName);
		//START

		// Deletion takes place here

		for (File file : files) {
			try {
				RegionFile regionFile = new RegionFile(file);
				// You are now in a region file.
				String temp = file.getName();
				temp = temp.substring(2, temp.length() - 4);
				String mcaName[] = temp.split("\\.");
				int regionX = Integer.valueOf(mcaName[0]);
				int regionZ = Integer.valueOf(mcaName[1]);
				counter++;
				System.out.println("Progress: " + counter + "/" + totalFiles + ". Chunks deleted: " + chunksDeleted);
				for (int chunkX = 0; chunkX < 32; chunkX++) {
					for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
						if (!safeChunks.contains("" + chunkX + "_" + chunkZ + "_" + regionX + "_" + regionZ)
								&& regionFile.hasChunk(chunkX, chunkZ)) {
							try {
								regionFile.deleteChunk(chunkX, chunkZ);
//							System.out.println("Deleted: " + chunkX + ", " + chunkZ);
								chunksDeleted++;
							} catch (IOException e1) {
//									e1.printStackTrace();
								System.err.println("Could not delete chunk at: " + chunkX + ", " + chunkZ + "! Ignoring it!");
								continue;
							}

						}
					}
				}
				try {
					regionFile.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			} catch (IndexOutOfBoundsException e1) {
				System.err.println("\tIMPORTANT INFO: Could not access correctly region file: " + file.getName() + "! Ignoring it!");
				continue;
			}
		}

		//END
		System.out.println("Current settings of deleting");


		String tempStrings = "";
		for (Option o : options.getOptionsList()) {
			for (String s : o.getBlockIDs())
				tempStrings = "" + tempStrings + s + ", ";
		}

		tempStrings.substring(tempStrings.indexOf(","));
		System.out.println("Total whitelisted block ID Strings: " + tempStrings);
		System.out.println("World file:" + folderName);
		System.out.println(corruptedChunks);
		System.out.println(corruptedRegionFiles);
		System.out.println("\tChunks deleted: " + chunksDeleted);
		System.out.println("--------Finished deleting--------");

	}

	/**
	 * This method adds files to a list that ensures that they will NOT be
	 * deleted later on.
	 */
	private synchronized void markSafeChunks(RegionFile regionFile, int chunkX, int chunkZ, int radius, int regionX, int regionZ) {
		if (radius == 0) {
			boolean worked = safeChunks.add("" + chunkX + "_" + chunkZ + "_" + regionX + "_" + regionZ);

			if (worked) {
				chunksEnqueued++;
				totalChunksEnqueued++;
			}
		} else // sur stand for surrounding
			for (int surX = chunkX - radius; surX <= chunkX + radius; surX++) {
				for (int surZ = chunkZ - radius; surZ <= chunkZ + radius; surZ++) {
					boolean b = surX >= chunkX / 32 && surX < chunkX / 32 + 32; // chunkX / 32 always gives 0.. if it doesn't, we deal with it later.
					b &= surZ >= chunkZ / 32 && surZ < chunkZ / 32 + 32; // chunkX / 32 + 32 always gives 32.. if it doesnt't, we deal with it later.
					boolean c = false;
					int finalX = surX, finalZ = surZ, finalRegionX = regionX, finalRegionZ = regionZ;
					if (surX < chunkX / 32) {
						finalX += 32;
						finalRegionX--;
						c = true;
					} else if (surX > chunkX / 32 + 31) {
						finalX -= 32;
						finalRegionX++;
						c = true;
					}
					if (surZ < chunkZ / 32) {
						finalZ += 32;
						finalRegionZ--;
						c = true;
					} else if (surZ > chunkZ / 32 + 31) {
						finalZ -= 32;
						finalRegionZ++;
						c = true;
					}
		
					if (c || (b && regionFile.hasChunk(surX, surZ))) {

						String toAdd = "" + finalX + "_" + finalZ + "_" + finalRegionX + "_" + finalRegionZ;
						boolean worked = safeChunks.add(toAdd);
						if (worked) {
							chunksEnqueued++;
							totalChunksEnqueued++;
						}

					}

				}
			}

	}

	/**
	 * Method to return the 4-bit nibble in a byte array at the given index. If
	 * the index is even, it returns the low nibble. If the index is odd, it
	 * returns the high nibble (whatever a nibble is).
	 * 
	 * @param arr
	 *                  The array to search in
	 * @param index
	 *                  The index to access in the array
	 */
	@Deprecated
	private byte nibble4(byte[] arr, int index) {
		return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
	}
}
