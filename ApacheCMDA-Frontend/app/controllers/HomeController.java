/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Post;
import models.PostAndComments;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import util.APICall;
import views.html.network.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HomeController extends Controller {
    final static Form<User> userForm = Form.form(User.class);

    public static Result myHome() {
        String viewerId = null;
        try {
             viewerId = session("current_user");
        } catch (Exception e) {
            System.out.println("session error");
            e.printStackTrace();
        }
        if (viewerId == null || viewerId.isEmpty()) {
            return redirect("/login");
        }
        List<PostAndComments> postAndComments = Post.get(viewerId);
        List<User> users = User.getFollowers(viewerId);
        int follow=1;
        for(User u : users) {
            if (viewerId.equals(String.valueOf(u.getId()))) {
                follow = 0;
            }
        }
        List<User> peopleYouMayFollow = User.getPeopleYouMayFollow(viewerId);

        return ok(home.render(User.get(viewerId), userForm, users, peopleYouMayFollow, viewerId, postAndComments, follow));
    }

    public static Result home(String id) {
        String viewerId = null;
        try {
            viewerId = session("current_user");
        } catch (Exception e) {
            System.out.println("session error");
            e.printStackTrace();
        }
        if (viewerId == null || viewerId.isEmpty()) {
            return redirect("/login");
        }
        List<PostAndComments> postAndComments = Post.get(id);
        List<User> users = User.getFollowers(id);
        int follow=1;
        for(User u : users) {
            if (viewerId.equals(String.valueOf(u.getId()))) {
                follow = 0;
            }
        }

        List<User> peopleYouMayFollow = User.getPeopleYouMayFollow(viewerId);
        return ok(home.render(User.get(id), userForm, users, peopleYouMayFollow, viewerId, postAndComments, follow));
    }

    public static Result logout() {
        session().remove("current_user");
        session().remove("current_token");
        return redirect("/login");
    }

    public static Result login() {
        Form<User> dc = userForm.bindFromRequest();
        ObjectNode jsonData = Json.newObject();
        User user = new User();
        try {
            jsonData.put("username", dc.field("username").value());
            jsonData.put("password", dc.field("password").value());
            user = User.verifyAndGet(jsonData);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Application.flashMsg(APICall.createResponse(APICall.ResponseType.CONVERSIONERROR));
        } catch (NumberFormatException e) {
            user.setUserName(dc.field("username").value());
            user.setPassword(dc.field("password").value());
//            return ok(login.render(user, userForm, e.getMessage()));
            return redirect(controllers.routes.LoginController.login(user.getUserName(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            Application.flashMsg(APICall.createResponse(APICall.ResponseType.UNKNOWN));
        }
        session("current_user", String.valueOf(user.getId()));
        session("current_token", user.getToken());
        List<User> users = User.getFollowers(String.valueOf(user.getId()));
        String viewerId = String.valueOf(user.getId());
        int follow = 1;
        for (User u : users) {
            if (viewerId.equals(String.valueOf(u.getId()))) {
                follow = 0;
            }
        }

        List<User> peopleYouMayFollow = User.getPeopleYouMayFollow(viewerId);

        return ok(home.render(user, userForm, users, peopleYouMayFollow, viewerId, Post.get(String.valueOf(user.getId())), follow));
    }

    public static Result follow(String source, String target) {
        User.follow(source, target);
        return redirect("/network/home/" + target);
    }

    public static Result unfollow(String source, String target) {
        User.unfollow(source, target);
        return redirect("/network/home/" + target);
    }

    public static Result followers(String id) {
        List<User> users = User.getFollowers(id);
        return ok(followers.render(users));
    }

}
