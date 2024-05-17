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
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;


import com.example.androidexample.Chat.ChatActivity;
import com.example.androidexample.EditQuestions.EditQuestionsActivity;
import com.example.androidexample.Groups.GroupPageActivity;
import com.example.androidexample.Login.LoginPageActivity;
import com.example.androidexample.Study.StudyActivity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RidaSystemTest {

    private static final int SIMULATED_DELAY_MS = 1000;

    @Rule
    public ActivityScenarioRule<LoginPageActivity> loginScenarioRule = new ActivityScenarioRule<>(LoginPageActivity.class);

    @Test
    public void testLogin(){
        // Test that login works correctly and volley properly fetches user
        Intents.init();
        String username = "TestUser";
        String password = "password";
        // If logged in, we expect to be welcomed
        String expectedString = "Welcome back, TestUser!";
        loginScenarioRule.getScenario().onActivity(activity -> {

        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());    // Type in username
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());    // Type in password
        onView(withId(R.id.logInButton)).perform(click());  // Click on login button

        // Allow volley to get user
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        intended(allOf( // Check if made it to MainActivity
                hasComponent(MainActivity.class.getName())
        ));

        // Check if greeting
        onView(withId(R.id.greeting)).check(matches(withText(expectedString)));
        Intents.release();
    }
    @Test
    public void checkNavBar(){
        // Same login procedure so user object isn't invalid
        String username = "TestUser";
        String password = "password";
        loginScenarioRule.getScenario().onActivity(activity -> {

        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());    // Type in testString
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());    // Type in testString
        onView(withId(R.id.logInButton)).perform(click());  // Click on To Second Activity button

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        Intents.init();
        // Check every button on nav bar
        onView(withId(R.id.nav_study)).perform(click());
        intended(allOf(
                hasComponent(StudyActivity.class.getName())
        ));

        onView(withId(R.id.nav_edit)).perform(click());
        intended(allOf(
                hasComponent(EditQuestionsActivity.class.getName())
        ));

        onView(withId(R.id.nav_home)).perform(click());
        intended(allOf(
                hasComponent(MainActivity.class.getName())
        ));

        onView(withId(R.id.nav_groups)).perform(click());
        intended(allOf(
                hasComponent(GroupPageActivity.class.getName())
        ));

        onView(withId(R.id.nav_chat)).perform(click());
        intended(allOf(
                hasComponent(ChatActivity.class.getName())
        ));

        Intents.release();
    }

    @Test
    public void testAddFragment(){
        String username = "TestUser";
        String password = "password";
        loginScenarioRule.getScenario().onActivity(activity -> {
        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.logInButton)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Check that fragment to add questions opens properly
        onView(withId(R.id.nav_edit)).perform(click());
        onView(withId(R.id.btnNewQuestion)).perform(click());

        onView(withText("New Question")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }
    @Test
    public void testFilterFragment(){
        String username = "TestUser";
        String password = "password";
        loginScenarioRule.getScenario().onActivity(activity -> {
        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());    // Type in testString
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());    // Type in testString
        onView(withId(R.id.logInButton)).perform(click());  // Click on To Second Activity button

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Check that fragment to filter questions opens properly
        onView(withId(R.id.nav_edit)).perform(click());
        onView(withId(R.id.btnFilter)).perform(click());

        onView(withText("Filter Questions")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    @Test
    public void testChat(){
        String username = "TestUser";
        String password = "password";
        loginScenarioRule.getScenario().onActivity(activity -> {
        });

        onView(withId(R.id.emailField)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.logInButton)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        onView(withId(R.id.nav_chat)).perform(click());

        // Check that we're properly displaying number of online users
        String usersOnline = "1 online";
        onView(withId(R.id.howManyOnlineTxt)).check(matches(withText(usersOnline)));


        String message = "Hello!";
        onView(withId(R.id.et2)).perform(typeText(message), closeSoftKeyboard());
        onView(withId(R.id.bt2)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Check that message gets displayed
        onView(withText(message)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

}