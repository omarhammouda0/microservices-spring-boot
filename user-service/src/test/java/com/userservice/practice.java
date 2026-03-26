package com.userservice;

import com.userservice.entity.User;
import com.userservice.enums.Role;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class practice {

    public static void main(String[] args) {

        User u1 = User.builder ( ).id ( 1L ).name ( "Omar" ).email ( "omar@test.com" ).password ( "pass" ).role ( "USER" ).isActive ( true ).createdAt ( Instant.now ( ) ).build ( );
        User u2 = User.builder ( ).id ( 2L ).name ( "Anna" ).email ( "anna@test.com" ).password ( "pass" ).role ( "USER" ).isActive ( false ).createdAt ( Instant.now ( ) ).build ( );
        User u3 = User.builder ( ).id ( 3L ).name ( "Felix" ).email ( "felix@test.com" ).password ( "pass" ).role ( "ADMIN" ).isActive ( true ).createdAt ( Instant.now ( ) ).build ( );
        User u4 = User.builder ( ).id ( 4L ).name ( "Sara" ).email ( "sara@test.com" )
                .password ( "pass" ).role ( "ADMIN" ).isActive ( false )
                .createdAt ( Instant.now ( ).minus ( 10 , ChronoUnit.DAYS ) ).build ( );

        User u5 = User.builder ( ).id ( 5L ).name ( "Lena" ).email ( "lena@test.com" )
                .password ( "pass" ).role ( "MODERATOR" ).isActive ( true )
                .createdAt ( Instant.now ( ).minus ( 5 , ChronoUnit.DAYS ) ).build ( );

        User u6 = User.builder ( ).id ( 6L ).name ( "Karim" ).email ( null )  // null email for Optional practice
                .password ( "pass" ).role ( "USER" ).isActive ( false )
                .createdAt ( Instant.now ( ).minus ( 2 , ChronoUnit.DAYS ) ).build ( );

        User u7 = User.builder ( ).id ( 7L ).name ( "Mia" ).email ( "mia@test.com" )
                .password ( "pass" ).role ( "MODERATOR" ).isActive ( true )
                .createdAt ( Instant.now ( ).minus ( 1 , ChronoUnit.DAYS ) ).build ( );

        List<User> users = List.of ( u1 , u2 , u3 , u4 , u5 , u6 , u7 );

        practice p = new practice ( );
        System.out.println ( p.getCountOfUsersByActive ( users ) );

//        Predicate<User> activeUser = User::getIsActive;
//        users.stream().filter( activeUser );


         Function <User , String> returnUserEmail = User::getEmail;

         users.stream ()
                 .map ( returnUserEmail );
    }


//    Write a Function<User, String> that takes a user and returns their email.
//    Assign it to a variable and use it in a stream map.


//    Exercise 1
//    Write a Predicate<User> that returns true if the user is active.
//    Assign it to a variable and use it in a stream filter.


    public Map <String , Double> getAverageIdByRole (List<User> users) {

        return users
                .stream ()
                .collect ( groupingBy ( User::getRole  ,
                        Collectors.averagingLong ( User::getId ) ));

    }

    public String getEmailOfTheFirstInactiveUser(List<User> users) {

        return users.stream ( )
                .filter ( user -> user.getIsActive ( ) == false )
                .findFirst ( )
                .map ( User::getEmail )
                .orElseThrow ( () -> new RuntimeException ( "No inactive user found" ) );
    }

    public Map <String, List<String> > getUsersGroupedByRole(List<User> users) {

        return users
                .stream ( )
                .collect ( groupingBy ( User::getRole , mapping ( User::getName , Collectors.toList ( ) ) ) );

    }

    public Map<Boolean, List<String>> getUsersGroupedByActive(List<User> users) {

        return users.stream ( )
                .collect ( partitioningBy ( User::getIsActive ,
                        mapping ( User::getName , Collectors.toList ( ) ) ) );

    }

    public Map <String , Long> getUsersCountByRole (List<User> users) {

        return users.stream ()
                .collect ( groupingBy ( User::getRole , Collectors.counting () )  );

    }

    public Map<String, Optional<User>> getOldestUserByRole(List<User> users) {

        return users.stream ( )
                .collect ( groupingBy ( User::getRole , Collectors.minBy
                        ( Comparator.comparing(User::getCreatedAt)  ) ) );




    }

    public Map <String , String> getAllUsersNamesByRole (List<User> users) {

        return users
                .stream ()
                .collect ( groupingBy ( User::getRole ,
                        mapping ( User::getName , Collectors.joining ( "," ) )
                ) );

    }

    public Map <Boolean , Long> getCountOfUsersByActive (List<User> users) {

        return users.stream ()
                .collect ( partitioningBy (
                        User::getIsActive ,
                        Collectors.counting ())

                );
    }

}

//Exercise 12
//From the user list, partition users into active and inactive,
//and for each partition return the count of users.

//Exercise 11
//From the user list, return a Map<String, String> where the key is the role
// and the value is a comma-separated string of all user names in that role.
//For example: "USER" -> "Omar, Anna, Karim"

//Partition users into two groups — active and inactive.
//Return a map where true maps to the list of active user
//names and false maps to the list of inactive user names.

//Exercise 4
//Group all users by their role.
// Print each role and the names of users belonging to it.


//
//
//Exercise 3
//From the list, get the email of the first inactive user.
//If none exists, throw an exception with the message "No inactive user found".

//        public String returnFirstAdmin (List<User> users) {
//
//            return users.stream ( )
//                    .filter ( user -> user.getRole ( ).equals ( "ADMIN" ) )
//                    .findFirst ( )
//                    .map ( User::getName )
//                    .orElse ( "No admin found" );


//    public String returnEmail (User user) {
//
//        var email = Optional.ofNullable ( user.getEmail () );
//        return email.orElse ( "no-email@default.com" );
//    }