package componenets.enums;

public enum HeaderParameter {

    AUTHORIZATION("Authorization"),

    CONTENT_TYPE("Content-Type"),

    CONNECTION("Connection");

    private String parameter;

    HeaderParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
