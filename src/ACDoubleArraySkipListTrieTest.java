import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ACDoubleArraySkipListTrieTest {
	public static void main(String[] args) throws IOException {
		ACDoubleArraySkipListTrie acDoubleArraySkipListTrie = new ACDoubleArraySkipListTrie();
		
		String str = "C:\\Users\\Deng\\Desktop\\ahocorasick\\benchmark\\cn\\dictionary.txt";
		Path path = FileSystems.getDefault().getPath(str);
		
		ArrayList<String> keys = (ArrayList<String>) Files.readAllLines(path);
		
		long start = System.nanoTime();
		acDoubleArraySkipListTrie.build(keys);
		long end = System.nanoTime();
		System.out.println("Build time: " + (end - start) + "ns");
		
		// UTF-8
		String tmp = "C:\\Users\\Deng\\Desktop\\ahocorasick\\benchmark\\cn\\text.txt";
		String content = new String(Files.readAllBytes(Paths.get(tmp/*"C:\\Users\\Deng\\Desktop\\ahocorasick\\benchmark\\cn\\text.txt"*/)), "UTF-8");
		// System.out.println(content);
		start = System.nanoTime();
		List<Range> v = acDoubleArraySkipListTrie.parseText(content);
		end = System.nanoTime();
		System.out.println("Match time: " + (end - start) + "ns");
		System.out.println("Matched number: " + v.size());
		
		start = System.nanoTime();
		ArrayList<Range> finalResult = acDoubleArraySkipListTrie.coverText(content);
		System.out.println("here");
		end = System.nanoTime();
		System.out.println(end - start);
		if (finalResult == null) {
			System.exit(0);
		}
		for (int i = 0; i < finalResult.size(); i++) {
//			System.out.println(finalResult.get(i).getStart() + " -> " + finalResult.get(i).getEnd());
		}
		System.out.println(finalResult.size());
		int i;
		Scanner scanner = new Scanner(System.in);
		i = scanner.nextInt();
		while (i != -1) {
			System.out.println(content.charAt(i));
			i = scanner.nextInt();
		}
		
		PrintWriter writer = new PrintWriter("C:\\Users\\Deng\\Desktop\\ahocorasick\\benchmark\\en\\good_419.txt", "UTF-8");
		Iterator<Range> iterator = v.iterator();
		while (iterator.hasNext()) {
//			System.out.println(iterator.next());
//			writer.println(iterator.next());
		}
		
//		new Scanner(System.in).nextLine();
	}
}