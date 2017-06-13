package uno.meng;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uno.meng.db.CUID;

public class TEST {
	
	public static void main(String[] args) throws SQLException, IOException {
		CUID cuid = new CUID();
		cuid.SearchReOpened(1);
	}
}

