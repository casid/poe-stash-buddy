# poe-stash-buddy

[![Build Status](https://travis-ci.org/casid/poe-stash-buddy.svg?branch=master)](https://travis-ci.org/casid/poe-stash-buddy)
[![Coverage Status](https://coveralls.io/repos/github/casid/poe-stash-buddy/badge.svg?branch=master)](https://coveralls.io/github/casid/poe-stash-buddy?branch=master)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/casid/poe-stash-buddy/master/LICENSE)

A little script to automatically insert currency into your stash. GGG plz add this as in-game feature つ ◕_◕ ༽つ

**Caution:** as some users on [reddit](https://www.reddit.com/r/pathofexile/comments/7n89bo/a_script_to_automatically_insert_currency_into) have pointed out, this is against PoE's ToS. Use at your own risk!

![Preview](https://github.com/casid/poe-stash-buddy/raw/master/preview.gif "Preview")

## Usage
- run poe-stash-buddy.exe
- run PoE, collect loot and go to your stash
- Press **CTR + SHIFT + A** to insert loot from inventory to stash

## Setup
- Download the latest [release](https://github.com/casid/poe-stash-buddy/releases)
- Unzip anywhere on your harddrive
- run poe-stash-buddy.exe
- run PoE and go to your stash
- ensure your stash tabs are visible
- take a screenshot of the game 
- enter the following values in the settings dialog:

### Inventory area and offsets
This is the area you are after:
![Inventory Area](https://github.com/casid/poe-stash-buddy/raw/master/inventory-area.png "Inventory Area")

Values for common resolutions
| Resolution | Inventory area | Offset |
| --- | --- | --- |
| 3840x2160 | 2546, 1176, 1260, 524 | 3, 3 |
| 1920x1080 | 1273, 588, 630, 262 | 1, 1 |

**Behold:** it's pretty essential to get these values as precisely as possible for the script to work properly.

### Stash tab locations
Use roughly the center of each stash tab you want to activate.

### Ignored slots (optional)
Click the inventory slots you don't want to have inserted in your stash. For instance your portal / wisdom scroll locations.
