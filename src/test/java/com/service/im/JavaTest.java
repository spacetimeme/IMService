package com.service.im;

public class JavaTest {

    public static void main(String[] args) throws Exception {
        Mode mode = new Mode("1", "1");
        System.out.println(mode.field + "  " + mode.name);
        Mode m = (Mode) mode.clone();
        System.out.println(m.field + "  " + m.name);
    }

    public static final class Mode implements Cloneable {
        private String field;
        public String name;

        public Mode(String field, String name) {
            this.field = field;
            this.name = name;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

}
