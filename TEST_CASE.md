# Test Scenarios

## END TO END TEST @LargeTest

### [ListStoryActivityEndToEndTest](./app/src/androidTest/java/com/dicoding/storyapp/ui/activity/ListStoryActivityEndToEndTest.kt)

#### loadListStory()

- **Assumption**: User is logged in.
- **Verify**:
    - The List of Stories is visible.
    - The ListStoryActivity opens successfully.
    - RecyclerView scrolls properly.
    - SwipeRefreshLayout is displayed.
    - Button to add a story is visible.
    - Button to view stories on Google Map is visible.
    - Perform a click action on the first item in the RecyclerView.

#### loadDetailStory()

- **Assumption**: User is logged in.
- **Verify**:
    - The List of Stories is visible.
    - Perform a click action on the first item in the RecyclerView.
    - Story description is displayed.
    - Sender's name is displayed.
    - Posting time of the story is displayed.
    - [Further verification steps are needed.]

#### loadStoryMap()

- **Assumption**: User is logged in and has allowed the application to use location services.
- **Verify**:
    - The List of Stories is visible.
    - Perform a click action on the button to view stories on Google Map.
    - Map is displayed.

#### loadAddStory()

- **Assumption**: User is logged in.
- **Verify**:
    - The List of Stories is visible.
    - Perform a click action on the button to post a story.
    - Image preview area is displayed.
    - Button to add location is visible.
    - Button to open the camera is visible.
    - Button to open the gallery is visible.
    - Button to upload story is displayed.

---

## INTEGRATION TEST @MediumTest

### [ListStoryActivityTest](./app/src/androidTest/java/com/dicoding/storyapp/ui/activity/ListStoryActivityTest.kt)

#### getStory_Success()

*[Check for successful data retrieval of stories from the network]*

- **Verify**:
    - Stories are displayed.
    - A story containing the keyword "Zekken" is displayed.
    - Scrolling is functional.
    - A story containing the keyword "ya" is displayed.

#### getStory_Error()

*[Check for error in data retrieval of stories from the network]*

- **Verify**:
    - Stories are displayed if available in the local database.
    - TextView "Oops.. something went wrong. Check your connection." is displayed.

---

## UNIT TEST

### [StoryRepositoryTest](./app/src/test/java/com/dicoding/storyapp/data/repository/StoryRepositoryTest.kt)

- When the `register()` function is called, it should not return a null value.
- When the `login()` function is called, it should not return a null value and should return user
  details including name, user ID, and token.
- When the `getStoryMap()` function is called, it should not return a null value and should return
  story data.
- When the `postStory()` function is called, it should not return a null value.
- When the `getPagingStories()` function is called, it should not return a null value and should
  return `PagingData`.

### [AddStoryViewModelTest](./app/src/test/java/com/dicoding/storyapp/ui/viewmodel/AddStoryViewModelTest.kt)

- When successfully adding a story:
    - `ResultResponse.Success` should be true.
    - `expectedResponse` should equal `ResultResponse.Success(dummyResponse)`.
    - `expectedResponse` and `actualResponse` should be the same.
- When failing to add a story:
    - `ResultResponse.Error` should be false.
    - `expectedResponse` should equal `ResultResponse.Error(dummyResponseError)`.
    - `actualResponse` and `ResultResponse.Error` should be the same.

### [DetailStoryViewModelTest](./app/src/test/java/com/dicoding/storyapp/ui/viewmodel/DetailStoryViewModelTest.kt)

- When successfully displaying story data:
    - `expectedStory` should equal `dummyStory`.
    - `actualStory` should equal `itemStory`.
    - `expectedStory` and `actualStory` should be the same.

### [ListStoryViewModelTest](./app/src/test/java/com/dicoding/storyapp/ui/viewmodel/ListStoryViewModelTest.kt)

- When successfully retrieving a list of stories:
    - Ensure that the data is not empty.
    - Ensure that the size of the actual data matches that of the dummy data.

### [LoginViewModelTest](./app/src/test/java/com/dicoding/storyapp/ui/viewmodel/LoginViewModelTest.kt)

- When successfully logging in:
    - `ResultResponse.Success` should be true.
    - Ensure that `actualResponse` is not empty.
    - `actualResponse` should equal `ResultResponse.Success`.
    - `dummyResult` should match `actualResponse`, meaning the data returns the same values for user
      name, user ID, and token.
- When failing to log in:
    - `ResultResponse.Error` should be false.
    - Ensure that `actualResponse` is not empty.
    - `actualResponse` should equal `ResultResponse.Error`, meaning it returns the same error data.

### [MainViewModelTest](./app/src/test/java/com/dicoding/storyapp/ui/viewmodel/MainViewModelTest.kt)

- When successfully retrieving user data from local storage:
    - Ensure that the local data is not empty.
    - Local data should match `dummyUserModel`.
- When successfully logging out:
    - The logout process with `mainViewModel` should match `userPreference`.

### [MapsViewModelTest](./app/src/test/java/com/dicoding/storyapp/ui/viewmodel/MapsViewModelTest.kt)

- When successfully retrieving story map data:
    - `ResultResponse.Success` should be true.
    - Ensure that `actualStory` is not empty.
    - `actualStory` should equal `ResultResponse.Success`.
    - Ensure the size of the actual data (`actualStory`) matches `dummyMaps`.
- When failing to retrieve story map data:
    - `ResultResponse.Error` should be false.
    - Ensure that `actualStory` is not empty.
    - `actualStory` should equal `ResultResponse.Error`.

### [RegisterViewModelTest](./app/src/test/java/com/dicoding/storyapp/ui/viewmodel/RegisterViewModelTest.kt)

- When successfully registering:
    - `ResultResponse.Success` should be true.
    - Ensure that `actualResponse` is not empty.
    - `actualResponse` should equal `ResultResponse.Success`.
    - Ensure that `dummyResponse` matches `actualResponse`.
- When failing to register:
    - `ResultResponse.Error` should be false.
    - Ensure that `actualResponse` is not empty.
    - `actualResponse` should equal `ResultResponse.Error`.
