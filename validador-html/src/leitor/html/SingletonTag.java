package leitor.html;

import java.util.Arrays;

public enum SingletonTag {

	META, //
	BASE, //
	BR, //
	COL, //
	COMMAND, //
	EMBED, //
	HR, //
	IMG, //
	INPUT, //
	LINK, //
	PARAM, //
	SOURCE, //
	DOCTYPE {

		@Override
		public String value() {
			return "!DOCTYPE";
		}

	};

	public String value() {
		return this.name();
	}

	public static boolean isSingletonTag(String tag) {
		return Arrays.asList(values()) //
				.stream() //
				.filter(v -> v.value().equalsIgnoreCase(tag)) //
				.findAny() //
				.isPresent();
	}

}
