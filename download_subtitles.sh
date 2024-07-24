#!/bin/bash

mkdir -p subtitles
cd subtitles || exit

video_urls=("https://www.youtube.com/watch?v=o7NlGCZw62k"
"https://www.youtube.com/watch?v=5JiQJV5MgAs"
"https://www.youtube.com/watch?v=mTwWJMMJqeY"
"https://www.youtube.com/watch?v=6Wth726sOoY"
"https://www.youtube.com/watch?v=cfEz6gGxOrY"
"https://www.youtube.com/watch?v=bP2NvoDOTZM"
"https://www.youtube.com/watch?v=cu3ti0LSrmo"
"https://www.youtube.com/watch?v=NIQE70vhu4E"
"https://www.youtube.com/watch?v=XAl2hxfKNTU"
"https://www.youtube.com/watch?v=t4ujOHZywg8"
"https://www.youtube.com/watch?v=mwGrpAxywBA"
"https://www.youtube.com/watch?v=9RNbeyEEqMo"
"https://www.youtube.com/watch?v=gpeSm-8FK4c"
"https://www.youtube.com/watch?v=IjASV_KZ2Q8"
"https://www.youtube.com/watch?v=wdDmCAZGJ68"
"https://www.youtube.com/watch?v=a1LuALwgPXE"
"https://www.youtube.com/watch?v=E45D_dXLh2E"
"https://www.youtube.com/watch?v=ILJ6M8PIbvE"
"https://www.youtube.com/watch?v=dQr8wrQNc6o"
"https://www.youtube.com/watch?v=TTS7LXKoBoo"
"https://www.youtube.com/watch?v=6YjLBAaGmsU"
"https://www.youtube.com/watch?v=xsu7-4_qqhA"
"https://www.youtube.com/watch?v=fNydG_6DfH0"
"https://www.youtube.com/watch?v=c047XcW-xAk"
"https://www.youtube.com/watch?v=3Z1D22c75y4"
"https://www.youtube.com/watch?v=vB5hLkhcEU4"
"https://www.youtube.com/watch?v=cWn4vRYfk5o"
"https://www.youtube.com/watch?v=9RJIRDZEHSs"
"https://www.youtube.com/watch?v=TCMM-LKXhmk"
"https://www.youtube.com/watch?v=xQamOBDw7Mw"
"https://www.youtube.com/watch?v=DzyvAuX7QDI"
"https://www.youtube.com/watch?v=yy9es0Nisck"
"https://www.youtube.com/watch?v=zIHLpUnMqf8"
"https://www.youtube.com/watch?v=1gLxL_eO2c8"
"https://www.youtube.com/watch?v=BVcEOQTtPpc"
"https://www.youtube.com/watch?v=gpCtA7CDHG4")

for url in "${video_urls[@]}"
do
    youtube-dl --write-auto-sub --sub-lang en --skip-download "$url"
done

touch subtitles-all.txt

cat *.vtt >> subtitles-all.txt