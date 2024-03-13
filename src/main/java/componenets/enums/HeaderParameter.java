package componenets.enums;

public enum HeaderParameter {

    AUTHORIZATION("Bearer 17a915d98bc061595c53aa898006e9e63a8d5935bfe8d316b0399ebbd31af775"),

    CONTENT_TYPE("application/json"),

    CONNECTION("keep-alive");

    private String parameter;

    HeaderParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
