package com.eslam.csp1401_test

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventsChanges(

	@field:SerializedName("@odata.nextLink")
	val odataNextLink: String? = null,

	@field:SerializedName("@odata.deltaLink")
	val odataDeltaLink: String? = null,

	@field:SerializedName("@odata.context")
	val odataContext: String? = null,

	@field:SerializedName("value")
	val events: List<EventItem?>? = null
) : Parcelable



@Parcelize
data class Range(

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("numberOfOccurrences")
	val numberOfOccurrences: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("recurrenceTimeZone")
	val recurrenceTimeZone: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null
) : Parcelable



