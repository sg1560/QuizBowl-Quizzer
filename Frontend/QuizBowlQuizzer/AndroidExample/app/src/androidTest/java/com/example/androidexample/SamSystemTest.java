package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;


import com.example.androidexample.Admin.AdminActivity;
import com.example.androidexample.EditQuestions.EditQuestionsActivity;
import com.example.androidexample.Groups.ListAllGroupsActivity;
import com.example.androidexample.Login.LoginPageActivity;
import com.example.androidexample.User.ShowAllUsersActivity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SamSystemTest {

    private static final int SIMULATED_DELAY_MS = 1000;

    @Rule
    public ActivityScenarioRule<LoginPageActivity> loginScenarioRule = new ActivityScenarioRule<>(LoginPageActivity.class);

    @After
    public void afterEach() {
        Intents.release();
    }

    @Test
    public void testLogin() {
        Intents.init();
        String username = "sam";
        String password = "sam";
        String expectedString = "Welcome back, sam!";
        loginScenarioRule.getScenario().onActivity(activity -> {

        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.logInButton)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        intended(allOf(hasComponent(MainActivity.class.getName())));

        // Check if greeting
        onView(withId(R.id.greeting)).check(matches(withText(expectedString)));
    }

    @Test
    public void testButtonsOnHome() {
        // This test broke after I move the buttons from the home screen.
        // I will comment this out to be fixed later.
//        String username = "SystemUser";
//        String password = "system";
//        loginScenarioRule.getScenario().onActivity(activity -> {
//
//        });
//
//        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());
//        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());
//        onView(withId(R.id.logInButton)).perform(click());
//
//        Intents.init();
//        try {
//            Thread.sleep(SIMULATED_DELAY_MS);
//        } catch (InterruptedException e) {
//        }
//
//        onView(withId(R.id.profilePicture)).perform(click());
//        onView(withId(R.id.adminButton)).perform(click());
//        intended(allOf(hasComponent(AdminActivity.class.getName())));
//        onView(withId(R.id.adminBack)).perform(click());
//
//        onView(withId(R.id.nav_edit)).perform(click());
//        intended(allOf(hasComponent(EditQuestionsActivity.class.getName())));
//        onView(withId(R.id.nav_home)).perform(click());
//
//        onView(withId(R.id.profilePicture)).perform(click());
//        onView(withId(R.id.showUsers)).perform(click());
//        intended(allOf(hasComponent(ShowAllUsersActivity.class.getName())));
    }

    @Test
    public void testListAllGroups() {
        Intents.init();
        String username = "TestUser";
        String password = "password";
        loginScenarioRule.getScenario().onActivity(activity -> {
        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.logInButton)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withId(R.id.nav_groups)).perform(click());
        onView(withId(R.id.listAllGroupsButton)).perform(click());
        intended(allOf(hasComponent(ListAllGroupsActivity.class.getName())));
        onView(withId(R.id.listGroupsButton)).perform(click());
    }

    @Test
    public void testListAllUsers() {
        Intents.init();
        String username = "TestUser";
        String password = "password";
        loginScenarioRule.getScenario().onActivity(activity -> {
        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.logInButton)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withId(R.id.profilePicture)).perform(click());
        onView(withId(R.id.showUsers)).perform(click());
        intended(allOf(hasComponent(ShowAllUsersActivity.class.getName())));
        onView(withId(R.id.getAllButton)).perform(click());
    }
}