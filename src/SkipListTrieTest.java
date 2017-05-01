import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class SkipListTrieTest {
	
	public static void main(String[] args) throws IOException {
		
		String[] beta = new String[] { "her", "their", "eye", "iris", "he", "is" };
		
		SkipListTrie mSkipListTrie = new SkipListTrie();
//		ArrayList<String> kArrayList = new ArrayList<>(Arrays.asList(beta));
//		mSkipListTrie.build(kArrayList);
//		System.out.println(mSkipListTrie);
		
//		String text = "theye";
//		ArrayList<Integer> ranges = mSkipListTrie.coverText(text);
//		for (int i = 0; i < ranges.size(); i+=2) {
//			System.out.println(ranges.get(i) + " -> " + ranges.get(i+1));
//		}
//		System.exit(0);
		
		ArrayList<String> keys = new ArrayList<>();
		
		String str = "C:\\Users\\Deng\\Desktop\\ahocorasick\\benchmark\\cn\\dictionary.txt";
		Path path = FileSystems.getDefault().getPath(str);
		
		keys = (ArrayList<String>) Files.readAllLines(path);
		
		long start = System.nanoTime();
		mSkipListTrie.build(keys);
		long afterBuild = System.nanoTime();
		System.out.println("Time: " + (afterBuild - start) + "ns");
		
		// UTF-8 matters: 2017/4/26
		String content = new String(Files.readAllBytes(Paths.get("C:\\Users\\Deng\\Desktop\\ahocorasick\\benchmark\\cn\\text.txt")), "UTF-8");
		
		start = System.nanoTime();
		int count = mSkipListTrie.match(content);
		afterBuild = System.nanoTime();
		System.out.println("Match time: " + (afterBuild - start) + "ns");
		System.out.println("Total matched words: " + count);
		
		
		ArrayList<Range> rangest = mSkipListTrie.coverText(content);
		for (int i = 0; i < rangest.size(); i++) {
//			System.out.println(rangest.get(i).getStart() + " -> " + rangest.get(i).getEnd());
		}
		System.out.println(rangest.size());
		
		new Scanner(System.in).nextLine();
	}
}