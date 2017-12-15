package com.kaltura.playkit.samples.eventsregistration;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.samples.eventsregistration.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PlayerEventsTest {

    private static final long SEEK_POSITION = 30000;

    private MainActivity mActivity;
    private ViewInteraction playButton;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        mActivity = mActivityTestRule.getActivity();

        mActivity.mEventListener = new EventListener() {
            @Override
            public void onPlayerInit() {
                //mActivityTestRule.getActivity().player.play();
            }

            @Override
            public void onPlayerStart(PlayerEvent event) {
                switch (event.type) {
                    case PLAYING:
                        assertTrue(mActivity.player.isPlaying());
                        break;
                    case PAUSE:
                        assertFalse(mActivity.player.isPlaying());
                        break;
                    case SEEKED:
                        assertEquals(SEEK_POSITION, mActivity.player.getCurrentPosition());
                        break;
                    case PLAYBACK_RATE_CHANGED:
                        PlayerEvent.PlaybackRateChanged playbackRateChanged = (PlayerEvent.PlaybackRateChanged) event;
                        //TODO there are no way to get rate from player
                        //assertEquals(playbackRateChanged.rate, mActivity.player.);
                        break;
                }
            }
        };

        playButton = onView(
                allOf(withId(R.id.play_pause_button), withText("Play"),
                        childAtPosition(
                                allOf(withId(R.id.buttons_container),
                                        childAtPosition(
                                                withId(R.id.activity_main),
                                                1)),
                                0),
                        isDisplayed()));
        playButton.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        playButton = onView(
                allOf(withId(R.id.play_pause_button), withText("Pause"),
                        childAtPosition(
                                allOf(withId(R.id.buttons_container),
                                        childAtPosition(
                                                withId(R.id.activity_main),
                                                1)),
                                0),
                        isDisplayed()));
        playButton.perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mActivity.player.seekTo(SEEK_POSITION);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
