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
 1. [snackprogressbar](https://github.com/tingyik90/snackprogressbar) by tingyik90, Apache License
 1. [libcgss](https://github.com/hozuki/libcgss) by hozuki, MIT License

# References

## Extract db files
```shell script
IFS=$'\n'; (for name in $(file a/* | grep SQLite | cut -d: -f1); do echo $name; done) | cut -d: -f1 | tar -cvf db.tar -T -
```

## CSV scheme (2020.12.19 update - witch mode)

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
   93. 93: change tempo (default=100)
4. startPos, finishPos: start and end position from left.
5. status:
   0. 0: normal
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
   
## Skill_type

1. Perfect Score bonus 
2. Perfect, Great score bonus
3. NONE
4. 콤보 보너스 4                  콤보
5. 판강 레어                             판정
6. 판강 스알                             판정
7. 판강 쓰알 4 + 2 + 1                   판정
8. MISS까지 커버되는 판강 (Not implemented)
9. 콤보 유지                                 콤보유지
10. NONE
11. NONE
12. 데미지 가드                                       라이프
13. NONE
14. 오버로드 8 + 4 + 2    스코어               콤보유지  라이프
15. 컨센 8+ 4+ 2 + 1     스코어                               판정
16. 앵콜 16
17. 라이프회복 16 + 1                                 라이프
18. NONE
19. NONE
20. 부스트
21. Cute focus
22. Cool focus
23. Passion focus            스코어   콤보
24. 올라운드                     콤보                  라이프
25. 라이프스파클                  콤보
26. 트리콜로로 시너지        스코어  콤보                  라이프
27. 코디네이트            스코어   콤보
28. 롱 액트             스코어 1, 2
29. 플릭 액트            스코어 1, 2
30. 슬라이드 액트        스코어 1, 2
31. 튜닝                       콤보     판정
32. Cute ensemble
33. Cool ensemble
34. Passion ensemble
35. Vocal Motif
36. Dance Motif
37. Visual Motif           스코어
38. 심포니
39. 얼터네이트          콤보 스코어
40. refrain
41. Cinderella Magic 

## leader_skill type

1. Cute voice30
2. Cute step30
3. Cute makeup30
4. Cute brilliance10
5. Cute energy10
6. Cool voice30
7. Cool step30
8. Cool makeup30
9. Cool brilliance10
10. Cool energy10
11. Passion voice30
12. Passion step30
13. Passion makeup30
14. Passion brilliance10
15. Passion energy10
16. Shiny voice24
17. Shiny step24
18. Shiny makeup24
19. Shiny brilliance8
20. Shiny energy8
21. Cute ability15
22. Cool ability15
23. Passion ability15
24. Cute voice60
25. Cute step60
26. Cute make60
27. Cute brilliance30
28. Cute energy20
29. Cool voice60 
30. Cool step60 
31. Cool make60 
32. Cool brilliance30 
33. Cool energy20 
34. Passion voice60 
35. Passion step60
36. Passion make60
37. Passion brilliance30
38. Passion energy20
39. Shiny voice48 
40. Shiny step48 
41. Shiny makeup48 
42. Shiny brilliance16 
43. Shiny energy16 
44. Cute ability30
45. Cool ability30
46. Passion ability30
47. Cute voice90
48. Cute step90
49. Cute make90
50. Cute brilliance40
51. Cute energy30
52. Cool voice90
53. Cool step90
54. Cool make90
55. Cool brilliance40
56. Cool energy30
57. Passion voice90
58. Passion step90
59. Passion make90
60. Passion brilliance40
61. Passion energy30
62. Shiny voice90
63. Shiny step90
64. Shiny makeup90
65. Shiny brilliance40
66. Shiny energy30
67. Cute ability40
68. Cool ability40
69. Passion ability40
70. Tricolor voice100
71. Tricolor step100
72. Tricolor makeup100
73. Tricolor ability50
74. Cute princess50
75. Cool princess50
76. Passion princess50
77. Cute cheer40
78. Cool cheer40
79. Passion cheer40
80. Fortune Present
81. Cinderella charm40
82. Tricolor voice80
83. Tricolor step80
84. Tricolor makeup80
85. Christmas Present
86. Cute princess35
87. Cool princess35
88. Passion princess35
89. Cute cross cool
90. Cute cross passion
91. Cool cross cute
92. Cool cross passion
93. Passion cross cute
94. Passion cross cool
101. Cute unizon55
102. Cool unizon55
103. Passion unizon55
104. Resonance Voice
105. Resonance Step
106. Resonance Make
107. Cute cross cool
108. Cute cross passion
109. Cool cross cute
110. Cool cross passion
111. Passion cross cute
112. Passion cross cool
113. Cinderella yell
114. Tricolor ability40
115. Cinderella charm32
116. World level
117. Cinderella with you
118. Cinderella breath