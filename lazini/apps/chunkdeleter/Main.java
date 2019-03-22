package lazini.apps.chunkdeleter;

public class Main {

	Options options;
	Deleter deleter;

	public Main() {
		options = new Options();
		deleter = new Deleter(this);
	}

	public static void main(String[] args) {
		Main main = new Main();

		main.deleter.loadWorld();
		System.out.println("\n");

		main.deleter.enqueue();
		System.out.println("\n");
    
		main.deleter.delete();
		System.out.println("\n");

	}

	public Options getOptions() {
		return options;
	}
}
