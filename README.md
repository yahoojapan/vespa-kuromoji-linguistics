# Vespa Linguistics with Kuromoji Tokenizer

## Overview

This package provides Japanese tokenizer with Vespa using Kuromoji.
Kuromoji is one of the famous Japanese tokenizer, it is implemented by Java and used by various services such as Solr, Elasticsearch, and so on.
For more details, please see official website of Kuromoji.

* [Kuromoji](http://www.atilika.org/)


## Create Package

### Requirement

JDK (>= 1.8) and maven are required to build package.

### Build

Execute mvn command as below, and you can get package as target/kuromoji-linguistics-${VERSION}-deploy.jar

```
$ mvn package -Dvespa.version='6.214.72'    # You can specify 6.214.72 or later.
```

## Use Package

### Deploy

Put the built package to components directory of your service. If there is no components directory, create it. For example, the structure will be like below with sampleapps.

* sampleapps/search/music/
    * services.xml
    * components/
        * kuromoji-linguistics-${VERSION}-deploy.jar

### Configuration

Because the package will be used by searcher and indexer, it is recommended to define &lt;component&gt; in all &lt;jdisc&gt; sections of services.xml.

```
<container id="container" version="1.0">
    <component id="kuromoji" class="jp.co.yahoo.vespa.language.lib.kuromoji.KuromojiLinguistics" bundle="kuromoji-linguistics">
        <config name="language.lib.kuromoji.kuromoji">
            <mode>search</search>
            <ignore_case>true</ignore_case>
        </config>
    </component>
</container>
```

You can configure package by &lt;config name="language.lib.kuromoji.kuromoji"&gt; (optional). Parameters and default settings are below.

|parameter|type|default|description|
|:--------|:---|:------|:----------|
|mode|string|search|mode of Kuromoji (normal OR search OR extended)|
|kanji.length_threshold|int|2|TODO|
|kanji.penalty|int|3000|TODO|
|other.length_threshold|int|7|TODO|
|other.penalty|int|1700|TODO|
|nakaguro_split|bool|false|TODO|
|user_dict|string|-|path of user dictionary|
|tokenlist_name|string|default|target specialtokens name|
|all_language|bool|false|apply kuromoji tokenizer to all language or only Japanese|
|ignore_case|bool|true|ignore upper/lower case difference|


### Activate

Simply use deploy command to activate package. For example, commands will be like below with sampleapps.

```
$ vespa-deploy prepare sampleapps/search/music/
$ vespa-deploy activate
```

Now, you can use the tokenizer with "language=ja" options !

## License

Code licensed under the Apache 2.0 license. See LICENSE for terms.

## Contributor License Agreement

This project requires contributors to agree to a [Contributor License
Agreement (CLA)](https://gist.github.com/ydnjp/3095832f100d5c3d2592).

Note that only for contributions to the vespa-kuromoji-linguistics repository on the GitHub (https://github.com/yahoojapan/vespa-kuromoji-linguistics),
the contributors of them shall be deemed to have agreed to the CLA without individual written agreements.
