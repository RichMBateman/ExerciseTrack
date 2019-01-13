package com.bateman.rich.exercisetrack.datamodel;

import android.provider.BaseColumns;

public class ExerciseEntry {
    private int m_id;
    private String m_name;
    private static final Contract s_contract = new Contract();

    public static Contract getContract() {return s_contract;}

    static class Contract extends DatabaseContractBase {
        private Contract() {
            super(ExerciseAppProvider.CONTENT_AUTHORITY_URI, ExerciseAppProvider.CONTENT_AUTHORITY, "ExerciseEntries");
        }

        static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_NAME = "Name";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }
}
