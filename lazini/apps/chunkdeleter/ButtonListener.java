package lazini.apps.chunkdeleter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import lazini.apps.chunkdeleter.mojang.chunk.storage.RegionFile;
import lazini.apps.chunkdeleter.mojang.nbt.CompoundTag;
import lazini.apps.chunkdeleter.mojang.nbt.ListTag;
import lazini.apps.chunkdeleter.mojang.nbt.Tag;

public class ButtonListener implements ActionListener {

	private GUI gui;
	private File mcaDir;
	private int blockIDs[], safeChunks;

	protected ButtonListener(GUI gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
//			System.out.println("tadaa");
			try {
				blockIDs = gui.getRawBlockIDs();
			} catch (NumberFormatException ex) {
				return; //gui.append(Type.WARNING, "Insert integers only, separated with commas! All other characters are not allowed!!");
			}

			mcaDir = new File(gui.getWorldPath(), "region");
			safeChunks = gui.getSafeChunksNumber();

			if (!mcaDir.isDirectory())
				System.out.println("The world folder you provided is not valid!");
			else {
				for (File mca : mcaDir.listFiles()) {
					if (mca.getName().endsWith("mca")) {
						// You are now in a region file.
						String temp = mca.getName();
						temp = temp.substring(2, temp.length() - 4);
						String mcaName[] = temp.split("\\.");
						int regionX = Integer.valueOf(mcaName[0]);
						int regionZ = Integer.valueOf(mcaName[1]);
						RegionFile regionFile = new RegionFile(mca);

						for (int chunkX = 0; chunkX < 32; chunkX++) {
							for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
								DataInputStream chunk = regionFile.getChunkDataInputStream(chunkX, chunkZ);
								if (regionFile.hasChunk(chunkX, chunkZ))

									try {
										Tag root = Tag.readNamedTag(chunk);

										CompoundTag level = root.getCompound("Level");
										ListTag sections = level.getList("Sections");

										for (int i = 0; i < sections.size(); i++) {
											CompoundTag section = (CompoundTag) sections.get(i);
											byte[] blocksArray = section.getByteArray("Blocks");
											byte[] addsArray = section.getByteArray("Add");
											byte Y = section.getByte("Y");

											for (int y = 0; y < 16; y++) {
												for (int z = 0; z < 16; z++) {
													for (int x = 0; x < 16; x++) {
														int realX = regionX * 32 + chunkX * 16 + x;
														int realY = Y * 16 + y;
														int realZ = regionZ * 32 + chunkZ * 16 + z;
														// Copied from Chunk Format page.
														int BlockPos = (y * 16 * 16) + (z * 16) + (x);
														byte BlockID_a = blocksArray[BlockPos];
														short BlockID = BlockID_a;
														if (addsArray.length != 0) {
															byte BlockID_b = nibble4(addsArray, BlockPos);
															BlockID = (short) (BlockID_a + (BlockID_b << 8));
														}
														for (int block : blockIDs) {
															if (BlockID_a == block && BlockID_a == 35) {
																JOptionPane.showMessageDialog(gui.getFrame(),
																		"Found wool, it fucking works!");
																return;
															}

														}

													}
												}
											}
										}
									} catch (IOException e1) {
										e1.printStackTrace();
									}
							}
						}

					}
				}
			}
			JOptionPane.showMessageDialog(gui.getFrame(), "Chunks deleted: 0", "Deletion completed", JOptionPane.INFORMATION_MESSAGE);

		}
	}

	/**
	 * Method to return the 4-bit nibble in a byte array at the given index. If
	 * the index is even, it returns the low nibble. If the index is odd, it
	 * returns the high nibble
	 * 
	 * @param arr
	 *            The array to search in
	 * @param index
	 *            The index to access in the array
	 */
	private byte nibble4(byte[] arr, int index) {
		return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
	}
}
