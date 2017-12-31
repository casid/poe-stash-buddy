# poe-stash-buddy

[![Build Status](https://travis-ci.org/casid/poe-stash-buddy.svg?branch=master)](https://travis-ci.org/casid/poe-stash-buddy)
[![Coverage Status](https://coveralls.io/repos/github/casid/poe-stash-buddy/badge.svg?branch=master)](https://coveralls.io/github/casid/poe-stash-buddy?branch=master)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/casid/poe-stash-buddy/master/LICENSE)

A little script to automatically insert currency into your stash. GGG plz add this as in-game feature つ ◕_◕ ༽つ

![Preview](https://github.com/casid/poe-stash-buddy/raw/master/preview.gif "Preview")

## Setup
- Download the latest [release](https://github.com/casid/poe-stash-buddy/releases)
- Unzip anywhere on your harddrive
- run poe-stash-buddy.exe
- run PoE and go to your stash
- ensure your stash tabs are visible
- take a screenshot of the game 
- enter the following values in the settings dialog

## Measure inventory area and offsets
This is the area you are after:
![Inventory Area](https://github.com/casid/poe-stash-buddy/raw/master/inventory-area.png "Inventory Area")

On my 3840x2160 display the inventory area is 2546, 1176, 1260, 524 with an offset of 3, 3

**Behold** it's pretty essential to get these values as precisely as possible for the script to work properly.

## Measure stash tab locations
Use roughly the center of each stash tab you want to activate.

## Usage
- run poe-stash-buddy.exe
- run PoE, collect loot and go to your stash
- Press CTR + SHIFT + A to insert loot from inventory to stash
