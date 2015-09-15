# TweetMap

## Setup configurations.

TweetMapアプリはTwitter APIとGoogle Map APIを使用する.  
これらのAPIを使用するにはAPI Keyをそれぞれ取得・設定する必要がある.  
API Keyは`build.gradle`が`project.properties`を参照し,これをstring resourceに登録する.  
設定するAPI Keyは個別に取得しておくこと.  
各API Keyを`project.properties`に登録する方法は下記の通り. 

 1. Android Projectのルートディレクトリに`gradle.properties`ファイルを作成
 2. `project.properties`ファイルに下記のKey-Valueを登録する.

    | key                        | description                 |
    |----------------------------|-----------------------------|
    | gmap_api_key               | Google Map API Key          |
    | twitter_consumer.key       | Twitter Consumer Key        |
    | twitter_consumer.secretkey | Twitter Consumer Secret Key |

example)
```
# Google API
gmap_api_key=AIxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Twitter API
twitter_consumer_key=xxxxxxxxxxxxxxxxxxxxxxxxxxxx
twitter_consumer_secretkey=xxxxxxxxxxxxxxxxxxxxxx
```

## Copyright

    Copyright 2015 TweetMap. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
