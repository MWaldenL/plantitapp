package com.mobdeve.s15.group8.mobdeve_mp.model

class JournalDataHelper {
    fun fetchData(): ArrayList<Journal> {
        val sample1 = "Mauris pharetra id augue nec varius. Donec facilisis purus neque, id sodales eros convallis sed. Duis quis gravida erat. Fusce quis commodo libero. Etiam vel nunc eget tellus efficitur ultricies ut eu sem. Nulla vitae risus purus. Morbi dapibus tincidunt sem vel commodo."
        val sample2 = "Mauris consectetur, risus id vulputate pharetra, eros tortor pulvinar sapien, eget tempor felis est eget lectus. Aliquam erat volutpat. Pellentesque ut augue pharetra, blandit enim eu, sollicitudin tellus. Curabitur mattis porta tristique. Mauris vestibulum mollis augue a euismod."
        val sample3 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris bibendum eros sed nunc posuere tempor. Mauris ultricies nec lorem a vulputate. Ut eu lorem cursus, auctor ligula a, imperdiet ante. Phasellus consequat vestibulum molestie. Suspendisse finibus eget velit quis luctus."

        // TODO: Fetch journal data from firebase

        val data = ArrayList<Journal>()
        data.add(Journal(sample1, "January 1, 1999"))
        data.add(Journal(sample2, "February 69, 420"))
        data.add(Journal(sample3, "August 12, 7569"))

        return data
    }
}