package lazini.apps.chunkdeleter;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class GUI {

	private FlowLayout lay = new FlowLayout(FlowLayout.LEFT, 10, 20);
	private JFrame frame = new JFrame("Minecraft Chunk Cleaner - Lazini");
	private JTextField blockList = new JTextField("4, 20, 5, 89, 64, 71, 96, 167, 193, 195, 196,"
			+ "197, 324, 330, 427, 428, 429, 430, 431, 54, 130, 146, 61, 62, 27, 28, 66, 157, 57, 42, 41, 133, 45, 108, 44, 53, 67, 109, "
			+ "128, 134, 135, 136, 156, 163, 164, 180, 203, 43, 44, 126, 182, 181, 204, 205, 169, 91, 145", 50);
	private JTextField savePath = new JTextField(50);
	private JTextField safeChunks = new JTextField("1", 5);
	private final String userName = System.getProperty("user.name");
	private File path = new File("C:/Users/" + userName + "/AppData/Roaming/.minecraft/saves/World");
	private JButton start = new JButton("Start");
	private JButton cancel = new JButton("Cancel");

	private ButtonListener listener;

	public GUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(730, 640);
		frame.setLocationRelativeTo(null);
		savePath.setText(path.getAbsolutePath());
		frame.setLayout(lay);
		start.setVisible(true);
//		area.setPreferredSize(new Dimension(692, 350));
		frame.setResizable(false);
		blockList.setToolTipText("A list of block IDs that, if found in a chunk, that chunk will not be deleted. Separate block IDs with commas. Whitespace is allowed.");
		savePath.setToolTipText("Absolute path to the world folder you'd like this program to operate on.");
		listener = new ButtonListener(this);
		start.addActionListener(listener);
		start.setActionCommand("start");

		cancel.addActionListener(listener);
		cancel.setActionCommand("cancel");
		safeChunks.setToolTipText("Chunks' number that are marked as safe, around a chunk that has player-put blocks.");
		frame.add(savePath);
		frame.add(blockList);
		frame.add(safeChunks);
		frame.add(start);
		frame.add(cancel);
		frame.setVisible(true);
//		JOptionPane.showMessageDialog(frame, "Message\nhaha", "Title", JOptionPane.INFORMATION_MESSAGE);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

//	public void append(Type type, String text) {
//		area.append(type.getName() + "\t  -  " + text + "\n");
//		area.update(area.getGraphics());
//		area.setCaretPosition(area.getText().length() - 1);
//		scroller.update(scroller.getGraphics());
//		scroller.validate();
//	}
//
//	public void append(String text) {
//		append(Type.INFO, text);
//	}
//
//	protected enum Type {
//
//		INFO("INFO"), WARNING("WARNING");
//		private String name;
//
//		private Type(String name) {
//			this.name = name;
//		}
//
//		String getName() {
//			return name;
//
//		}
//	}

	public String getWorldPath() {
		return savePath.getText();
	}

//	public void setBlockIDs(int[] blockIDs) {
//		this.blockIDs = blockIDs;
//	}
//
//	public int[] getBlockIDs() {
//		return blockIDs;
//	}

	public int[] getRawBlockIDs() {
		String temp = blockList.getText().replaceAll(" ", "");
		String[] list = temp.split(",");
		int[] array = new int[list.length];
		for (int i = 0; i < list.length; i++) {
			array[i] = Integer.valueOf(list[i]);
		}
//		System.out.println(Arrays.toString(list));
		return array;

	}


	public int getSafeChunksNumber() {
		return Integer.valueOf(safeChunks.getText());
	}


}
