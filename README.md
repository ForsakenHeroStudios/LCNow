# LC Now
An events app for Lewis and Clark College with other helpful features

Current functionality
- Currently exists solely in Android form.
- Pulls events from LC events calendar. Exposed LiveWhale API helped tremendously with formatting.
- Each event displays its title, date, start and end time, location, description, and image (as available).
- Can display events for any day (with selection of day using a calendar view).
- Search for a specific event (simply checks if description or title contains the string entered by the user).
- Can format an email for you to send to Jason Feiner to request to add an event. The user fills in all fields, and tapping the request button takes the user to their email service of choice.
- Long hold on an event to set a heads-up notification about that event to appear one hour prior to the event.
- Tap the star next to a group to subscribe to it. Notifications for upcoming events in subscribed areas will automatically appear without the user manually selecting specific events

Current problems
- Will not run on our old tablet (API 21, which is the minimum we will support). Something with the bottom navigation widget isn't supported. This shouldn't be a major issue, as we plan to move away from a bottom navigation UI to a grid-like view as more features are added.
- Calendar View doesn't appear on emulated device (Galaxy Nexus API 27). This may or may not be an issue, as we will most likely be utilizing a more efficent means of selecting a certain day to view the events for.
- Some dates and/or start and end times of events display raw html, while others have just a date and time. We may want to implent a Web View here, or perhaps just search through the string to find the relevant info to display.
- Bottom navigation view icons shift up and down awkwardly, which again will not be much of a problem further down the road, as we will not be using this in our final UI.
- Keyboard is slow to close when tapping on other items or pressing enter, but this is most likely because we are using an emulator.

Next steps
- Allow the user to subscribe to certain filters.
- Make it easier (or at least more efficient) to post events. We will need to learn more about Live Whale and how events are posted using it. We may need cloud services to hold user accounts and log into Live Whale, although a local SQL database could do the trick (in this case, we would probably just display the Live Whale web page within the app, and not worry about making our own UI for it).
- Add meals tab for students to quickly view what will be served each day. 
- Integrate the Onion Rater into meal view (developed at LC Hackathon 2018 by a group of fellow LC students. Check it out here: bon.linusaur.us).
- Revive and integrate the PioTracker. We may need to toss an Arduino with a GPS and Cellular module in the Pio and build it from scratch, but Arduino is great so that won't be a problem.
- Add a feedback option. This is crucial. There will inevitably be many, many bugs in the first versions, so user feedback is invaluable. We want to make the most functional, efficient, and easy-to-use app possible. If scathing, anonymous reviews is what it takes to get there, so be it.
- Include a campus emergency services contact option.
- Include a campus concerns section. This should be anonymous, and perhaps be sent to ASLC or the appropriate group as specified by the user. This may require cloud hosting if we want to send user-to-user messages through the app, but this could possibly be accomplished with a third party email service.
- Include some service that could directly benefit professors, faculty, and all employed by Lewis and Clark, widening our user base to aid all of Lewis and Clark. Surveying these groups should help us discover what could benefit them the most.
- Perhaps a specific NSO tab should be added. As this is scheduled to be launched by NSO 2019, this could be very helpful.
- A map function with labeled buildings, and a search function for specific buildings would help new/prospective students. This could widen our user base further to prospective students.
- Some assistance with major/class schedule planning could be helpful as well.
- Integrate Moodle, Canvas? Some other service used by professors?
- Let students create personal reminders, goals within the app, to be used as a planner as well?
- Make it work on iOS...
- Add a schedule for various locations on campus in use (gym, library rooms, classrooms) as well as a way to "reserve" them. Perhaps have the user enter where they are currently working, then have a persistent notification in the notification center that the user can tap on to say they leave/are done.
- Have SQRC hours/who is currently working along with what they can help you with.
- Perhaps have a section where people ask questions about stuff and someone can answer (people who answer are awarded Pio Points...?)
- That same feature could be used to help form study groups
- Perhaps give people Pio Points for reserving and closing reservations (with a certain number of max points per day) to incentivize people to study and also make the service more useful to others looking for an open room
- Have a Pio Points leaderboard, give something to winners at the end of each semester (LC Now t-shirt?)
- Need an online store page
- option to watch an ad and get Pio Points? (Do we let ourselves stoop that low?)
- LC Now merch?
