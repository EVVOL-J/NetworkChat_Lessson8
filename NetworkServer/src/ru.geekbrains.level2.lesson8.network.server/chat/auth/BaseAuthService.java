package ru.geekbrains.level2.lesson8.network.server.chat.auth;

import ru.geekbrains.level2.lesson8.network.server.chat.user.User;


import java.util.List;


public class BaseAuthService implements AuthService {
//    private static final Map<String,String> USERS=new HashMap<>(){{
//        put("login1","pass1");
//        put("login2","pass2");
//        put("login4","pass3");
//    }};

    private static final List<User> USERS = List.of(
            new User("login1", "pass1", "Oleg"),
            new User("login2", "pass2", "Alexey"),
            new User("login3", "pass3", "Peter")
    );

    @Override
    public void start() {
        System.out.println("Auth service has been started");
    }

    @Override
    public void stop() {
        System.out.println("Auth service has been finished");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User user : USERS) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user.getUsername();
            }
        }

        return null;
    }
}
