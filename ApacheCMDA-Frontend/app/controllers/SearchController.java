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
import models.Keyword;
import models.Post;
import models.PostAndComments;
import models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.APICall;
import views.html.network.*;

import java.util.List;

import java.util.ArrayList;

public class SearchController extends Controller {
    final static Form<User> userForm = Form.form(User.class);

    public static Result searchPost(String id) {
        User user = User.get(id);
        List<User> users = User.getFollowers(id);
        String viewerId = session("current_user");
        int follow = 1;
        for (User u : users) {
            if (viewerId.equals(String.valueOf(u.getId()))) {
                follow = 0;
            }
        }
        //srch-term
        Form<User> dc = userForm.bindFromRequest();
        String keyword = dc.field("srch-term").value();
        keyword = keyword.replaceAll(" ", "_");
        Keyword.put(keyword);
        List<PostAndComments> searchedResult = Post.search(viewerId, keyword);
        return ok(searchPost.render(user, userForm, users, viewerId, searchedResult, follow));
    }

    public static Result searchUser(String id) {
        User user = User.get(id);
        List<User> users = User.getFollowers(id);
        String viewerId = session("current_user");
        int follow = 1;
        for (User u : users) {
            if (viewerId.equals(String.valueOf(u.getId()))) {
                follow = 0;
            }
        }
        //srch-term
        Form<User> dc = userForm.bindFromRequest();
        String keyword = dc.field("srch-term").value();
        keyword = keyword.replaceAll(" ", "_");
        Keyword.put(keyword);
        List<User> searchedResult = User.search(keyword);
        return ok(searchUser.render(user, userForm, users, viewerId, searchedResult, follow));
    }

    public static Result goTo(String source, String target){
        String viewerId = session("current_user");
        User user = User.get(target);
        List<PostAndComments> postsandComments = Post.get(target);
        List<User> users = User.getFollowers(target);
        int follow=1;
        for(User u : users){
            if(viewerId.equals(String.valueOf(u.getId()))){
                follow=0;
            }
        }

        List<User> peopleYouMayFollow = new ArrayList<User>();
        return ok(home.render(user, userForm, users, peopleYouMayFollow, source, postsandComments,follow));
    }
}
