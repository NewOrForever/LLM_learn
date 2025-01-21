package org.example.practice;

import java.util.List;
import java.util.function.Function;

/**
 * ClassName:UserService
 * Package:org.example.practice
 * Description: UserService tool 使用 functionCallback 的方式添加到 runtime ChatOptions 中
 *
 * @Date:2025/1/20 16:37
 * @Author:qs@1.com
 */
public class UserService implements Function<UserService.Request, List<UserService.User>> {

    public record Request(String date) {}

    @Override
    public List<User> apply(Request request) {
        System.out.println("执行UserService工具， 入参为：" + request.date);
        return List.of(new User("周瑜", "2025年1月1号"), new User("诸葛亮", "2025年1月2号"));
    }

    class User {
        private String username;
        private String registrationDate;

        public User(String username, String registrationDate) {
            this.username = username;
            this.registrationDate = registrationDate;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRegistrationDate() {
            return registrationDate;
        }

        public void setRegistrationDate(String registrationDate) {
            this.registrationDate = registrationDate;
        }
    }

}
