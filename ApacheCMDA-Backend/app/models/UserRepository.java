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
package models;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many standard
 * operations here.
 */
@Named
@Singleton
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByUserName(String userName);

    User findByEmail(String email);

    @Query(value = "select u.* from User u where u.id=?", nativeQuery = true)
    User findByID(long id);

    @Query(value = "insert into FollowMap values (?1,?2)", nativeQuery = true)
    void addFollower(long user1_ID, long user2_ID);

    @Query(value = "delete from FollowMap where user1_id in (?1) and user2_id in (?2)", nativeQuery = true)
    void deleteFollower(long user1_ID, long user2_ID);

    @Query(value = "select User.* from User, FollowMap where ((FollowMap.user2_id = ?1) and (User.id = FollowMap.user1_id))", nativeQuery = true)
    List<User> findFollowers(long user_ID);
}
