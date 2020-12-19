# DereInfo(デレステアクト曲 情報-데레스테 액트 곡 목록)

Deresute songs information

Maybe can be used as a reference when you hesitate whom with which `Act skill` to scout using normal scout tickets. 

**Supported Act skills: Long act, Flick act, and Slide act (롱, 플릭, 슬라이드 액트)(ロングアクト、フリクアクト、スライドアクト)**

[Release](https://github.com/KYHSGeekCode/DereInfo/releases)


# Features

1. Search for songs by
   1. Original names
   2. Romanji
   3. Hangul
   4. ID
2. View 
   1. Romanji name
   2. Composer
   3. Lyricist
   4. Type of the song
   5. Levels
   6. scores(fumens) of the music
    1. debut, regular, pro, master, master+, light, trick, piano, forte
3. Sort By
   1. Data order
   2. Name
   3. Total notes
   4. Percentage of long notes
   5. Percentage of flick notes
   6. Percentage of slide notes
   7. Percentage of total notes
   8. Percentage of notes by time(7, 9, 11)
4. Filter by
   1. Type of the song
   2. Has MasterPlus
   3. Has smart mode
   4. Has grand mode


# Known bugs 

1. Filtering does not work. 

# Open Source
 1. [snackprogressbar](https://github.com/tingyik90/snackprogressbar) by tingyik90


# References

## Extract db files
```shell script
IFS=$'\n'; (for name in $(file a/* | grep SQLite | cut -d: -f1); do echo $name; done) | cut -d: -f1 | tar -cvf db.tar -T -
```

## CSV scheme (2020.12.19 update - witch mode

1. id: primary key, **does NOT indicate the index of the notes.**
2. sec: The time when the note reaches the judge line.
3. type:
   1. normal
   2. hold (long)
   3. slide
   4. grand normal note
   5. grand slide note
   6. grand left flick
   7. grand right flick
   8. witch accident
   9. witch decorator
   93. change tempo (default=100)
4. startPos, finishPos: start and end position from left.
5. status:
   0. normal
   1. normal left flick
   2. normal right flick
   3. or raw note width in grand mode.
   4. or tempo if type is 93.
   5. or color if type is 9.
6. sync: whether they are treated as equal if two notes are similar timing (floating point)
7. groupId: For long(hold), slide, decorator notes.
8. visible: when the note appear(>0) or disappear(<0), judge=100 in witch
9. size: the size of note in witch
10. distance: helper for engine after changing tempo
   