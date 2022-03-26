package com.davidkazad.chantlouange.config.utils;

import java.util.Date;

import android.content.Context;
import android.content.res.Resources;

import com.davidkazad.chantlouange.R;

public class TimeAgo {

	protected Context context;

	public TimeAgo(Context context) {
		this.context = context;
	}

	public String using(Date date) {
		return using(date.getTime());
	}

	public String using(long millis) {
		long diff = new Date().getTime() - millis;

		Resources r = context.getResources();

		String prefix = r.getString(R.string.time_ago_prefix);
		String suffix = r.getString(R.string.time_ago_suffix);

		double seconds = Math.abs(diff) / 1000;
		double minutes = seconds / 60;
		double hours = minutes / 60;
		double days = hours / 24;
		double years = days / 365;

		String words;

		if (seconds < 45) {
			words = r.getString(R.string.time_ago_seconds);
		} else if (seconds < 90) {
			words = r.getString(R.string.time_ago_minute);
		} else if (minutes < 45) {
			words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
		} else if (minutes < 90) {
			words = r.getString(R.string.time_ago_hour);
		} else if (hours < 24) {
			words = r.getString(R.string.time_ago_hours, Math.round(hours));
		} else if (hours < 42) {
			words = r.getString(R.string.time_ago_day);
		} else if (days < 30) {
			words = r.getString(R.string.time_ago_days, Math.round(days));
		} else if (days < 45) {
			words = r.getString(R.string.time_ago_month);
		} else if (days < 365) {
			words = r.getString(R.string.time_ago_months, Math.round(days / 30));
		} else if (years < 1.5) {
			words = r.getString(R.string.time_ago_year);
		} else {
			words = r.getString(R.string.time_ago_years, Math.round(years));
		}

		StringBuilder sb = new StringBuilder();

		if (prefix != null && prefix.length() > 0) {
			sb.append(prefix).append(" ");
		}

		sb.append(words);

		if (suffix != null && suffix.length() > 0) {
			sb.append(" ").append(suffix);
		}

		return sb.toString().trim();
	}
}