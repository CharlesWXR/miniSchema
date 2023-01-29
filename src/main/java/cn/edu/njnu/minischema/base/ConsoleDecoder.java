package cn.edu.njnu.minischema.base;

import java.util.Scanner;

public class ConsoleDecoder {
	private static final String Hinter = "->";
	private static final String Exit = "exit";

	private Decoder decoder = new Decoder();
	private Scanner scanner = new Scanner(System.in);

	public int run() {
		// Read from console and execute the code
		// 'exit' can exit from the loop
		System.out.print(Hinter);
		String code = scanner.nextLine();
		while (!code.toLowerCase().equals(Exit)) {
			decoder.execute(code);
			System.out.print(Hinter);
			code = scanner.nextLine();
		}
		return 1;
	}
}
