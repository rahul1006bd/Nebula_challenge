Nebula_challenge
Automation script for calculating the difference between the any two dates.
Patent Date Difference Calculator

This Java Selenium automation project extracts publication, grant, and filing dates from the [WIPO Pat-INFORMED](https://patinformed.wipo.int/) website for a given search keyword (pharmaceutical name), and calculates the number of days between the dates.

 Features

- Accepts search keyword as a command-line argument.
- Handles dynamic popups like cookie consent or terms agreement.
- Extracts and cleans date strings that may include annotations like (18 years ago).
- Calculates day differences between:
  - Publication Date and Grant Date
  - Publication Date and Filing Date
  - Grant Date and Filing Date
- Uses explicit/implicit waits instead of `Thread.sleep()` for better reliability.
- Includes error handling with helpful console logs.
- Modular, scalable, and commented code for easy maintenance.
